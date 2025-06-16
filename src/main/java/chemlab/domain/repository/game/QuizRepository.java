package chemlab.domain.repository.game;

import chemlab.domain.model.game.FormulaQuiz;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends MongoRepository<FormulaQuiz, ObjectId> {
    Optional<FormulaQuiz> findByFormula(String formula);
}
