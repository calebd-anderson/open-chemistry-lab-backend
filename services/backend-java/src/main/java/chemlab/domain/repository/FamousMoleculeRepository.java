package chemlab.domain.repository;

import chemlab.domain.model.chemistry.FamousMolecule;

import java.util.List;

public interface FamousMoleculeRepository {
    FamousMolecule findByFormula(String formula);
    List<FamousMolecule> findAll();
    FamousMolecule save(FamousMolecule famousMolecule);
}
