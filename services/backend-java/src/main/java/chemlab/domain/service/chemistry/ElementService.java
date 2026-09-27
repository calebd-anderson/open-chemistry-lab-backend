package chemlab.domain.service.chemistry;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.infrastructure.pubchem.PubChemElement;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ElementService {
    List<PubChemElement> getAllElements() throws FailedToLoadPTException;
    Optional<PubChemElement> getElementBySymbol(String symbol);
}
