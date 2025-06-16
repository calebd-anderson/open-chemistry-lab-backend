package chemlab.domain.repository.game;

import chemlab.domain.model.game.Quiz;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, ObjectId> {
    List<Quiz> findByQuestion(String question);

    List<Quiz> findByAnswer(String Answer);

    @Query("{ quizType: '?0' }")
    List<Quiz> findQuizByQuizType(String quizType);
}
