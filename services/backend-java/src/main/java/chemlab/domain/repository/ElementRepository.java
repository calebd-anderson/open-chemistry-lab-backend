package chemlab.domain.repository;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.infrastructure.pubchem.PubChemElement;

import java.util.List;
import java.util.Optional;

public interface ElementRepository {
    List<PubChemElement> findAll() throws FailedToLoadPTException;
    Optional<PubChemElement> findElementBySymbol(String symbol);
}
