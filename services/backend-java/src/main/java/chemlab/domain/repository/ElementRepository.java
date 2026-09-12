package chemlab.domain.repository;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.infrastructure.pubchem.PubChemElement;

import java.util.List;

public interface ElementRepository {
    List<PubChemElement> findAll() throws FailedToLoadPTException;
    PubChemElement findElementBySymbol(String symbol);
}
