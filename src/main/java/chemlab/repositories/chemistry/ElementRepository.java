// the repository class implements CRUD actions
package chemlab.repositories.chemistry;

import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.model.chemistry.Element;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ElementRepository {

    List<Element> findAll() throws FailedToLoadPTException;

    @Query("{symbol:'?0'}")
    Element findElementBySymbol(String symbol);

}
