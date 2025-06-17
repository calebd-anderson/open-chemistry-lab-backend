package chemlab.domain.repository.game;

import chemlab.domain.model.game.Quiz;
import chemlab.domain.model.game.QuizType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, ObjectId>, FormulaQuizRepository {
    Optional<Quiz> findByQuizType(QuizType type);
}
