package chemlab.service.chemistry;

import chemlab.domain.model.PugApiDTO;
import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.chemistry.ReactionRepository;
import chemlab.domain.repository.user.UserReactionsRepo;
import chemlab.domain.user.RegisteredUserService;
import chemlab.exceptions.domain.PugApiException;
import chemlab.service.game.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

import static services.pubchem.PugApiConstants.*;

@Service
public class ReactionService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ReactionRepository reactionRepo;
    @Autowired
    private RegisteredUserService userService;
    @Autowired
    private UserReactionsRepo userReactionsRepo;
    @Autowired
    private QuizService quizService;

    /**
     * TODO:
     * - Add handler for when request to PUG API doesn't return element
     * - Add local variable for last request sent (6 second delay if within window of last request)
     * -
     */

    public boolean doesValueExistInRepo(String formula) {
        LOG.info("Checking if [{}] exists in db.", formula);
        if (!reactionRepo.findCompoundByFormula(formula).isEmpty()) {
            LOG.info("[{}] found in db.", formula);
            return true;
        } else {
            LOG.info("[{}] not found in db.", formula);
            return false;
        }
    }

    private Reaction retrieveCompoundFromRepo(String formula) {
        LOG.info("Value [{}] exists in repo, retrieving...", formula);
        return reactionRepo.findCompoundByFormula(formula).get(0);
    }

    private Reaction retrieveCompoundFromPugApi(String formula, Reaction reaction) throws PugApiException {
        LOG.info("Calling PUG API with argument: {}", formula);
        String pubChemUrl = PUG_PROLOG + PUG_INPUT + formula + PUG_OPERATION + PUG_OUTPUT;
        try {
            // LOG.info("Sending PugAPI url in service: {}", pubChemUrl);
            PugApiDTO pugApiValue = restTemplate.getForObject(pubChemUrl, PugApiDTO.class);
            assert pugApiValue != null;
            reaction.setTitle(pugApiValue.getFirstPropertyTitle());
            return reaction;
        } catch (HttpStatusCodeException exception) {
            // to do: parse the JSON returned as an error to get a useful response
            // LOG.error(exception.getResponseBodyAsString());
            // LOG.error("Received " + exception.getStatusCode().value() + " response code from PUG API.");
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new PugApiException("404 error from PugAPI url: " + pubChemUrl);
            } else
                throw new PugApiException("pug api error");
        }
    }

    public List<UserReaction> getCompoundsByUserId(String userId) {
        return userReactionsRepo.findReactionsByUserId(userId);
    }

    public Reaction validateInput(Reaction reaction) throws PugApiException {
        String formula = reaction.getFormula();
        LOG.info("Validating: [{}]", formula);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Reaction resultingReaction;
        // new discovery
        if (!doesValueExistInRepo(formula)) {
            // try formula with pubchem api
            resultingReaction = retrieveCompoundFromPugApi(formula, reaction);
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
            User user = userService.findUserByUsername(authentication.getName());
            // quizService.createNewQuizes(resultingReaction, user.getUserId(), "compound");
            // quizService.createNewQuizes(resultingReaction, user.getUserId(), "element");
            userReactionsRepo.saveReactionWithUser(user.getUserId(), resultingReaction);
        }
        resultingReaction = reactionRepo.save(resultingReaction);
        return resultingReaction;
    }
}
