package chemlab.domain.game;

import chemlab.model.game.UserQuiz;
import chemlab.model.shared.CreateQuizDto;
import chemlab.model.user.User;

import java.util.List;

public interface QuizService {
    void createQuiz(CreateQuizDto quiz, User user);
    List<UserQuiz> findQuizByUserId(String userId);
}
