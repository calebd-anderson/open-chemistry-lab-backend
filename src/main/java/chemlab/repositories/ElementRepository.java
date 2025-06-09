// the repository class implements CRUD actions
package chemlab.repositories;

import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.model.Element;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ElementRepository {

    List<Element> findAll() throws FailedToLoadPTException;

    @Query("{symbol:'?0'}")
    Element findElementBySymbol(String symbol);

}
