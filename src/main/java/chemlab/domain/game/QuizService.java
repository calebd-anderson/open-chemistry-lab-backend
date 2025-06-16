package chemlab.domain.game;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.game.FormulaQuiz;
import chemlab.domain.model.game.QuizType;
import org.bson.types.ObjectId;
import shared.CreateQuizDto;

import java.util.List;

public interface QuizService {
    FormulaQuiz createQuiz(CreateQuizDto quiz);
    List<FormulaQuiz> list();
    FormulaQuiz getQuizById(ObjectId id);
    List<FormulaQuiz> findQuizByUserId(String userId);
    FormulaQuiz getQuizByFormula(String formula);
}
