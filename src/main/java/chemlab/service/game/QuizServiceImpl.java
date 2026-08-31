package chemlab.service.game;

import chemlab.domain.game.QuizService;
import chemlab.model.game.UserQuiz;
import chemlab.model.shared.CreateQuizDto;
import chemlab.model.user.User;
import chemlab.repository.game.quiz.UserQuizRepository;
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
    private UserQuizRepository quizRepo;

    @Autowired
    UserReactionRepository userReactionRepo;

    @Autowired
    RegisteredUserRepository userRepo;

    public void createQuiz(CreateQuizDto quizDto, User user) {
        String q1 = "What is the name of this molecule: " + quizDto.getFormula() + "?";
        String a1 = quizDto.getReactionName();

        String q2 = "What is the formula for " + quizDto.getReactionName() + "?";
        String a2 = quizDto.getFormula();

        UserQuiz userQuiz1 = new UserQuiz();
        UserQuiz userQuiz2 = new UserQuiz();

        userQuiz1.setUserId(user.getUserId());
        userQuiz2.setUserId(user.getUserId());

        userQuiz1.setQuestion(q1);
        userQuiz1.setAnswer(a1);

        userQuiz2.setQuestion(q2);
        userQuiz2.setAnswer(a2);

        quizRepo.save(userQuiz1);
        quizRepo.save(userQuiz2);
    }

    public List<UserQuiz> findQuizByUserId(String userId) {
        List<UserQuiz> userQuizzes = quizRepo.findByUserId(userId);
        return userQuizzes;
    }
}
