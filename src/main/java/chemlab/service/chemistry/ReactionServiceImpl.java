package chemlab.service.chemistry;

import chemlab.domain.chemistry.ReactionService;
import services.pubchem.PubChemApiService;
import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.chemistry.ReactionRepository;
import chemlab.domain.repository.user.RegisteredUserRepository;
import chemlab.domain.repository.user.UserReactionsRepo;
import chemlab.exceptions.domain.PugApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class ReactionServiceImpl implements ReactionService {

    @Autowired
    private ReactionRepository reactionRepo;
    @Autowired
    private UserReactionsRepo userReactionsRepo;
    @Autowired
    private RegisteredUserRepository userRepo;

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
        if (!reactionRepo.findCompoundByFormula(formula).isEmpty()) {
            log.info("[{}] found in db.", formula);
            return true;
        } else {
            log.info("[{}] not found in db.", formula);
            return false;
        }
    }

    private Reaction retrieveCompoundFromRepo(String formula) {
        log.trace("Looking up [{}] in repo ...", formula);
        return reactionRepo.findCompoundByFormula(formula).get(0);
    }

    public List<UserReaction> getCompoundsByUserId(String userId) {
        return userReactionsRepo.findReactionsByUserId(userId);
    }

    public Reaction validateInput(Reaction reaction) throws PugApiException {
        String formula = reaction.getFormula();
        log.info("Validating: [{}]", formula);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Reaction resultingReaction;
        // new discovery
        if (!hasCompoundBeenDiscovered(formula)) {
            // try formula with PubChem api
            resultingReaction = pubChemApi.testFormula(formula, reaction);
            resultingReaction.setDiscoveredWhen(Instant.now());
            if (authentication != null && authentication.isAuthenticated()) {
                resultingReaction.setDiscoveredBy(authentication.getName());
            } else {
                resultingReaction.setDiscoveredBy("anonymous");
            }
        } else {
            // reaction already discovered
            resultingReaction = retrieveCompoundFromRepo(formula);
        }
        resultingReaction.setLastDiscoveredWhen(Instant.now());
        resultingReaction.setDiscoveryCount(resultingReaction.getDiscoveryCount() + 1);
        // set last discovered by
        if (authentication != null && authentication.isAuthenticated()) {
            resultingReaction.setLastDiscoveredBy(authentication.getName());
        } else {
            resultingReaction.setLastDiscoveredBy("anonymous");
        }

        // if user is logged in; create game data and save reaction from discovered reaction with the user
        if (authentication != null && authentication.isAuthenticated()) {
            // need to lookup user by username until able to add userid to JWT
            User user = userRepo.findRegisteredUserByUsername(authentication.getName());
            // quizService.createNewQuizes(resultingReaction, user.getUserId(), "compound");
            // quizService.createNewQuizes(resultingReaction, user.getUserId(), "element");
            userReactionsRepo.saveReactionWithUser(user.getUserId(), resultingReaction);
        }
        resultingReaction = reactionRepo.save(resultingReaction);
        return resultingReaction;
    }
}
