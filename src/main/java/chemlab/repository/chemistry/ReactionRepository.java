package chemlab.repository.chemistry;

import chemlab.model.chemistry.Reaction;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReactionRepository extends MongoRepository<Reaction, ObjectId> {
    Reaction findReactionByFormula(String formula);
}
