package chemlab.infrastructure.persistence.chemistry;

import chemlab.domain.model.chemistry.FamousMolecule;
import chemlab.domain.repository.FamousMoleculeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FamousMoleculePersistenceAdapter implements FamousMoleculeRepository {

    private final SpringDataFamousMoleculeRepository mongoRepository;

    @Override
    public FamousMolecule findByFormula(String formula) {
        return mongoRepository.findByFormula(formula);
    }

    @Override
    public List<FamousMolecule> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public FamousMolecule save(FamousMolecule famousMolecule) {
        return mongoRepository.save(famousMolecule);
    }
}
