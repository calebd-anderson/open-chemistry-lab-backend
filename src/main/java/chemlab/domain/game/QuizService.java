package chemlab.domain.game;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.game.Quiz;
import chemlab.domain.model.game.QuizType;
import org.bson.types.ObjectId;
import shared.CreateQuizDto;

import java.util.List;

public interface QuizService {
    List<ObjectId> createQuiz(CreateQuizDto quiz);
    List<Quiz> list();
    Quiz getQuizById(ObjectId id);
    void createNewQuizzes(Reaction reaction, QuizType quizType);
    List<Quiz> queryQuestions(String question);
    List<Quiz> queryAnswers(String answer);
    List<Quiz> findQuizByUserId(String userId);
    List<Quiz> findQuizByQuizType(String quizType);
}
