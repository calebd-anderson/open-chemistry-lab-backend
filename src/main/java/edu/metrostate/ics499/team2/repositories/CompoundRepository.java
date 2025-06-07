package edu.metrostate.ics499.team2.repositories;

import edu.metrostate.ics499.team2.model.Compound;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CompoundRepository extends MongoRepository<Compound, String> {
	
    @Query("{formula:'?0'}")
	List<Compound> findCompoundByFormula(String formula);

    @Query("{ userId: '?0' }")
    List<Compound> findCompoundByUserId(String userId);
}
