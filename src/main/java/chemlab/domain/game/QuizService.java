package chemlab.domain.game;

import chemlab.model.game.UserQuiz;

import java.util.List;

public interface QuizService {
    List<UserQuiz> findQuizByUserId(String userId);
}
