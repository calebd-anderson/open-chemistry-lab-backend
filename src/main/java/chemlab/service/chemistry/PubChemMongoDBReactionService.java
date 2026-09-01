package chemlab.service.chemistry;

import chemlab.domain.service.chemistry.ReactionService;
import chemlab.domain.service.user.UserReactionService;
import chemlab.infrastructure.pubchem.service.PubChemApiService;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.domain.model.user.User;
import chemlab.repository.chemistry.ReactionRepository;
import chemlab.repository.user.RegisteredUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class PubChemMongoDBReactionService implements ReactionService {

    @Autowired
    private ReactionRepository reactionRepo;
    @Autowired
    private RegisteredUserRepository userRepo;
    @Autowired
    private UserReactionService userReactionService;

    @Autowired
    private PubChemApiService pubChemApi;

    /**
     * TODO:
     * - Add handler for when request to PUG API doesn't return element
     * - Add local variable for last request sent (6 second delay if within window of last request)
     * -
     */

    public boolean hasCompoundBeenDiscovered(String formula) {
        log.info("Checking if [{}] exists in db.", formula);
        if (reactionRepo.findReactionByFormula(formula) != null) {
            log.info("[{}] found in db.", formula);
            return true;
        } else {
            log.info("[{}] not found in db.", formula);
            return false;
        }
    }

    private Reaction retrieveCompoundFromRepo(String formula) {
        log.info("Looking up [{}] in repo ...", formula);
        return reactionRepo.findReactionByFormula(formula);
    }

    public List<UserReaction> getCompoundsByUserId(String userId) {
        return userReactionService.findReactionsByUserId(userId);
    }

    public List<Reaction> findAllDiscoveredReactions() {
        return reactionRepo.findAll();
    }

    public Reaction validateInput(Reaction reaction) throws PugApiException {
        String formula = reaction.getFormula();
        log.info("Validating: [{}]", formula);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = (authentication != null && authentication.isAuthenticated());

        Reaction resultingReaction;
        // new discovery
        if (!hasCompoundBeenDiscovered(formula)) {
            // try formula with PubChem api
            resultingReaction = pubChemApi.testFormula(formula, reaction);
            resultingReaction.setFirstDiscoveredWhen(Instant.now());
            String name = authenticated ? authentication.getName() : "anonymous";
            log.info("Setting reaction discovered by: {}", name);
            resultingReaction.setFirstDiscoveredBy(name);

        } else {
            // reaction already discovered
            resultingReaction = retrieveCompoundFromRepo(formula);
        }
        resultingReaction.setLastDiscoveredWhen(Instant.now());
        resultingReaction.setDiscoveredCount(resultingReaction.getDiscoveredCount() + 1);
        // set last discovered by
        String discoveredBy = authenticated ? authentication.getName() : "anonymous";
        resultingReaction.setLastDiscoveredBy(discoveredBy);
        log.info("Updating reaction with formula: {}", resultingReaction.getFormula());
        resultingReaction = reactionRepo.save(resultingReaction);
        // if user is logged in; create game data and save reaction from discovered reaction with the user
        if (authenticated) {
            // need to lookup user by username until able to add userid to JWT
            log.info("Querying the db for user with username: {}", authentication.getName());
            User user = userRepo.findRegisteredUserByUsername(authentication.getName());
            log.info("Saving the {} reaction with the user.", resultingReaction.getFormula());
            userReactionService.saveReactionWithUser(user.getUserId(), resultingReaction);
        }
        log.info("Finished validating input.");
        return resultingReaction;
    }
}
