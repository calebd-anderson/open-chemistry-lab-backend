package chemlab.domain.game;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.game.Quiz;

import java.util.List;

public interface QuizService {
    Quiz createQuiz(Quiz quiz);
    void createQuizzes(List<Quiz> quizList);
    List<Quiz> list();
    Quiz getQuizById(String id);
    void createNewQuizzes(Reaction reaction, String userId, String quizType);
    List<Quiz> queryQuestions(String question);
    List<Quiz> queryAnswers(String answer);
    List<Quiz> findQuizByUserId(String userId);
    List<Quiz> findQuizByQuizType(String quizType);
    boolean isValid(Quiz obj);
}
