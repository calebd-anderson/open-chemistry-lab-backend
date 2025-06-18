// the repository class implements CRUD actions
package chemlab.repository.chemistry;

import chemlab.model.chemistry.Element;
import chemlab.exceptions.domain.FailedToLoadPTException;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElementRepository {
    List<Element> findAll() throws FailedToLoadPTException;

    @Query("{symbol:'?0'}")
    Element findElementBySymbol(String symbol);
}
