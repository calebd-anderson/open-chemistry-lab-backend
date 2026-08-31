package chemlab.service.game;

import chemlab.domain.game.QuizService;
import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.UserReaction;
import chemlab.model.game.UserQuiz;
import chemlab.model.user.User;
import chemlab.repository.user.RegisteredUserRepository;
import chemlab.repository.user.UserReactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuizServiceImpl implements QuizService {

    @Autowired
    UserReactionRepository userReactionRepo;

    @Autowired
    RegisteredUserRepository userRepo;

    private List<UserQuiz> generateQuizzes(String userId) {
        User user = userRepo.findRegisteredUserByUserId(userId);
        if (user != null) {
            List<UserReaction> userReactions = userReactionRepo.findByUserId(userId);
            List<UserQuiz> userQuizzes = new java.util.ArrayList<>();
            for(UserReaction userReaction : userReactions) {
                Reaction reaction = userReaction.getReaction();

                String q1 = "What is the name of this molecule: " + reaction.getFormula() + "?";
                String a1 = reaction.getTitle();
                userQuizzes.add(new UserQuiz(q1, a1));

                String q2 = "What is the formula for " + reaction.getTitle() + "?";
                String a2 = reaction.getFormula();
                userQuizzes.add(new UserQuiz(q2, a2));
            }
            return userQuizzes;
        }
        return new java.util.ArrayList<>(); // Return an empty list if user is not present
    }

    public List<UserQuiz> findQuizByUserId(String userId) {
        return generateQuizzes(userId);
    }
}
