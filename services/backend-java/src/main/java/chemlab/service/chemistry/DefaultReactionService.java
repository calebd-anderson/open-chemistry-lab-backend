package chemlab.service.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.ReactionRepository;
import chemlab.domain.repository.RegisteredUserRepository;
import chemlab.domain.service.chemistry.ReactionService;
import chemlab.domain.service.user.UserReactionService;
import chemlab.infrastructure.fastapiworker.ClusterMapRequest;
import chemlab.infrastructure.fastapiworker.service.FastApiWorkerService;
import chemlab.infrastructure.pubchem.PugApiResponse.FastformulaPropertiesResponse;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import chemlab.infrastructure.pubchem.service.PubChemApiService;
import chemlab.shared.requests.ReactionRequest;
import chemlab.shared.responses.ReactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DefaultReactionService implements ReactionService {

    @Autowired
    private ReactionRepository reactionRepo;
    @Autowired
    private RegisteredUserRepository userRepo;
    @Autowired
    private UserReactionService userReactionService;
    @Autowired
    private FastApiWorkerService fastApiWorkerService;

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

    public ReactionResponse createReaction(ReactionRequest payload) throws PugApiException {
        Reaction reaction = new Reaction(payload.getMappedPayload());

        String formula = reaction.getFormula();
        log.trace("Validating: [{}]", formula);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = (authentication != null && authentication.isAuthenticated());
        String discoveredBy = authenticated ? authentication.getName() : "anonymous";

        // new discovery
        if (!hasCompoundBeenDiscovered(formula)) {
            // try formula with PubChem api
            FastformulaPropertiesResponse pugApiResponse = pubChemApi.getFormulaProperties(formula);
            reaction.setTitle(pugApiResponse.getFirstPropertyTitle());
            reaction.setFirstDiscoveredWhen(Instant.now());
            log.trace("Setting reaction discovered by: {}", discoveredBy);
            reaction.setFirstDiscoveredBy(discoveredBy);
        } else {
            // reaction already discovered
            reaction = retrieveCompoundFromRepo(formula);
        }
        reaction.setLastDiscoveredWhen(Instant.now());
        reaction.setDiscoveredCount(reaction.getDiscoveredCount() + 1);
        // set last discovered by
        reaction.setLastDiscoveredBy(discoveredBy);
        log.trace("Updating reaction with formula: {}", reaction.getFormula());
        reaction = reactionRepo.save(reaction);
        // if user is logged in; create game data and save reaction from discovered reaction with the user
        if (authenticated) {
            // need to lookup user by username until able to add userid to JWT
            log.trace("Querying the db for user with username: {}", authentication.getName());
            Optional<User> user = userRepo.findByUsername(authentication.getName());
            log.trace("Saving the {} reaction with the user.", reaction.getFormula());
            userReactionService.saveReactionWithUser(user.get().getUserId(), reaction);
        }
        log.trace("Finished validating input.");
        ModelMapper modelMapper = new ModelMapper();
        ReactionResponse response = modelMapper.map(reaction, ReactionResponse.class);
        return response;
    }


    public List<ClusterMapRequest>  analyzeFormula(ReactionRequest payload) throws PugApiException, JsonProcessingException {
        log.trace("Analyzing formula in default reaction service.");
        Reaction reaction = new Reaction(payload.getMappedPayload());
        FastformulaPropertiesResponse pugApiResponse = pubChemApi.getFormulaProperties(reaction.getFormula());
        return fastApiWorkerService.analyzePubChemFastformulaProps(pugApiResponse);
    }
}
