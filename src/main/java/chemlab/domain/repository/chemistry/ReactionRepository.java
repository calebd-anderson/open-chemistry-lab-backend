package chemlab.domain.repository.chemistry;

import chemlab.model.chemistry.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReactionRepository extends MongoRepository<Reaction, String> {
    //    @Query("{formula:'?0'}")
    Reaction findReactionByFormula(String formula);
}
