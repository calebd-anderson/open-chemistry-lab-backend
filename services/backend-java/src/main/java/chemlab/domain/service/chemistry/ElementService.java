package chemlab.domain.service.chemistry;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.infrastructure.pubchem.PubChemElement;

import java.util.List;

public interface ElementService {
    List<PubChemElement> getAllElements() throws FailedToLoadPTException;
    PubChemElement getElementBySymbol(String symbol);
}
