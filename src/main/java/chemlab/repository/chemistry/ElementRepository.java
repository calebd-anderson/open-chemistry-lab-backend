package chemlab.repository.chemistry;

import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.model.chemistry.PubChemElement;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ElementRepository {
    List<PubChemElement> findAll() throws FailedToLoadPTException;
    @Query("{symbol:'?0'}")
    PubChemElement findElementBySymbol(String symbol);
}
