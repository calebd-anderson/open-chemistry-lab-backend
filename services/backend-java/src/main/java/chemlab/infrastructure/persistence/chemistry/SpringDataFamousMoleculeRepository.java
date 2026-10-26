package chemlab.infrastructure.persistence.chemistry;

import chemlab.domain.model.chemistry.FamousMolecule;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataFamousMoleculeRepository extends MongoRepository<FamousMolecule, String> {
    FamousMolecule findByFormula(String formula);
}
