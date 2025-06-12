// the repository class implements CRUD actions
package chemlab.domain.repository.chemistry;

import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.domain.model.chemistry.Element;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ElementRepository {
    List<Element> findAll() throws FailedToLoadPTException;

    @Query("{symbol:'?0'}")
    Element findElementBySymbol(String symbol);
}
