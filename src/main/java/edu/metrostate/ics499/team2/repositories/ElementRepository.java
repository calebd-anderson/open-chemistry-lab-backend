// the repository class implements CRUD actions
package edu.metrostate.ics499.team2.repositories;

import edu.metrostate.ics499.team2.exceptions.domain.FailedToLoadPTException;
import edu.metrostate.ics499.team2.model.Element;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ElementRepository {

    List<Element> findAll() throws FailedToLoadPTException;

    @Query("{symbol:'?0'}")
    Element findElementBySymbol(String symbol);

}
