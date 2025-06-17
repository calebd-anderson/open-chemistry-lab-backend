package chemlab.domain.game;

import chemlab.domain.model.game.ReactionQuiz;
import shared.CreateQuizDto;

import java.util.List;

public interface QuizService {
    ReactionQuiz createQuiz(CreateQuizDto quiz);
    List<ReactionQuiz> findQuizByUserId(String userId);
    ReactionQuiz getQuizByFormula(String formula);
}
