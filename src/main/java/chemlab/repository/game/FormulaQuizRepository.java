package chemlab.repository.game;

import chemlab.model.game.ReactionQuiz;

public interface FormulaQuizRepository {
//    @Query("{ 'question' : { $regex: ?0 } }")
    void createFormulaQuiz(ReactionQuiz quiz);
    ReactionQuiz findQuizByFormula(String formula);
//    @Query("{ 'question' : { $regex: ?0 } }")
    void saveFormulaQuiz(ReactionQuiz quiz);
}
