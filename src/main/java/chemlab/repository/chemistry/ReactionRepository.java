package chemlab.repository.chemistry;

import chemlab.model.chemistry.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReactionRepository extends MongoRepository<Reaction, String> {
    Reaction findReactionByFormula(String formula);
}
