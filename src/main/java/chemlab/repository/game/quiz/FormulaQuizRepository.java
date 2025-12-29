package chemlab.repository.game.quiz;

import chemlab.model.game.ReactionQuiz;

public interface FormulaQuizRepository {
    void createFormulaQuiz(ReactionQuiz quiz);
    ReactionQuiz findQuizByFormula(String formula);
    void saveFormulaQuiz(ReactionQuiz quiz);
}
