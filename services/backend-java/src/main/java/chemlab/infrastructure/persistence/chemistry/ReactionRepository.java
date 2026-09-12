package chemlab.infrastructure.persistence.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReactionRepository extends MongoRepository<Reaction, String> {
    Reaction findReactionByFormula(String formula);
}
