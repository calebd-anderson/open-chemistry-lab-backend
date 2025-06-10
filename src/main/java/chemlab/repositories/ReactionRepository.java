package chemlab.repositories;

import chemlab.model.Compound;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReactionRepository extends MongoRepository<Compound, String> {
	
    @Query("{formula:'?0'}")
	List<Compound> findCompoundByFormula(String formula);

    @Query("{ userId: '?0' }")
    List<Compound> findCompoundByUserId(String userId);
}
