package chemlab.repositories.chemistry;

import chemlab.model.chemistry.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReactionRepository extends MongoRepository<Reaction, String> {
	
    @Query("{formula:'?0'}")
	List<Reaction> findCompoundByFormula(String formula);

    @Query("{ userId: '?0' }")
    List<Reaction> findCompoundByUserId(String userId);
}
