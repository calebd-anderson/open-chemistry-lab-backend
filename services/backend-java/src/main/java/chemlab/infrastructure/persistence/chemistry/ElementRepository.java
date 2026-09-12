package chemlab.infrastructure.persistence.chemistry;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.infrastructure.pubchem.PubChemElement;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ElementRepository {
    List<PubChemElement> findAll() throws FailedToLoadPTException;
    @Query("{symbol:'?0'}")
    PubChemElement findElementBySymbol(String symbol);
}
