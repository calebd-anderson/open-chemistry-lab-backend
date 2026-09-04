package chemlab.domain.service.game;

import chemlab.domain.model.game.UserQuiz;

import java.util.List;

public interface QuizService {
    List<UserQuiz> findQuizByUserId(String userId);
}
