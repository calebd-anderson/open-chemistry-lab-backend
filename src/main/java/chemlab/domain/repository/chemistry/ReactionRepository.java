package chemlab.domain.repository.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReactionRepository extends MongoRepository<Reaction, String> {

    @Query("{formula:'?0'}")
    List<Reaction> findCompoundByFormula(String formula);
}
