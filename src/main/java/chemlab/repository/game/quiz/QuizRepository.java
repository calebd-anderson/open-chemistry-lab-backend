package chemlab.repository.game.quiz;

import chemlab.model.game.Quiz;
import chemlab.model.game.QuizType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface QuizRepository extends MongoRepository<Quiz, ObjectId>, FormulaQuizRepository {
    Optional<Quiz> findByQuizType(QuizType type);
}
