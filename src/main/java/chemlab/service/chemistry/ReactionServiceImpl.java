package chemlab.service.chemistry;

import chemlab.domain.chemistry.ReactionService;
import chemlab.domain.game.QuizService;
import chemlab.domain.user.UserReactionService;
import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.UserReaction;
import chemlab.model.game.ReactionQuiz;
import chemlab.model.user.User;
import chemlab.repository.chemistry.ReactionRepository;
import chemlab.repository.user.RegisteredUserRepository;
import infrastructure.pubchem.PubChemApiService;
import infrastructure.pubchem.exceptions.PugApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import shared.CreateQuizDto;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class ReactionServiceImpl implements ReactionService {

    @Autowired
    private ReactionRepository reactionRepo;
    @Autowired
    private RegisteredUserRepository userRepo;
    @Autowired
    private UserReactionService userReactionService;
    @Autowired
    private QuizService quizService;

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
                log.info("Setting reaction discovered by: {}", authentication.getName());
                resultingReaction.setDiscoveredBy(authentication.getName());
            } else {
                log.info("Setting reaction discovered by: {}", "anonymous");
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
        log.info("Updating reaction with formula: {}", resultingReaction.getFormula());
        resultingReaction = reactionRepo.save(resultingReaction);
        // if user is logged in; create game data and save reaction from discovered reaction with the user
        if (authentication != null && authentication.isAuthenticated()) {
            // need to lookup user by username until able to add userid to JWT
            log.info("Querying the db for user with username: {}", authentication.getName());
            User user = userRepo.findRegisteredUserByUsername(authentication.getName());
            log.info("Saving the {} reaction with the user.", resultingReaction.getFormula());
            userReactionService.saveReactionWithUser(user.getUserId(), resultingReaction);
            log.info("Creating the reaction quiz.");
            CreateQuizDto quizDto = new CreateQuizDto(resultingReaction.getFormula(), resultingReaction.getTitle());
            ReactionQuiz quiz = quizService.createQuiz(quizDto);
            log.info("Added a quiz for: {}", quiz.getReaction().getFormula());
        }
        log.info("Finished validating input.");
        return resultingReaction;
    }
}
