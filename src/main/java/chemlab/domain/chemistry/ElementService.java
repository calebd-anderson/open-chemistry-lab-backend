package chemlab.domain.chemistry;

import chemlab.domain.model.chemistry.Element;
import chemlab.exceptions.domain.FailedToLoadPTException;

import java.util.List;

public interface ElementService {
    List<Element> getAllElements() throws FailedToLoadPTException;
    Element getElementBySymbol(String symbol);
}
