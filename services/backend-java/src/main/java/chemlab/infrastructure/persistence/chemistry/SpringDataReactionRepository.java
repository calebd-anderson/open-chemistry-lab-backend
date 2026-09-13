package chemlab.infrastructure.persistence.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataReactionRepository extends MongoRepository<Reaction, String> {
    Reaction findReactionByFormula(String formula);
}
