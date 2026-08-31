package chemlab.domain.chemistry;

import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.model.chemistry.PubChemElement;

import java.util.List;

public interface ElementService {
    List<PubChemElement> getAllElements() throws FailedToLoadPTException;
    PubChemElement getElementBySymbol(String symbol);
}
