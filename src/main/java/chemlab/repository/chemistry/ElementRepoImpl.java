package chemlab.repository.chemistry;

import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.model.chemistry.Element;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Repository
@Slf4j
public class ElementRepoImpl implements ElementRepository {

    private final String PERIODIC_TABLE_PATH = "static/data/all_elements.json";

    @Override
    public List<Element> findAll() throws FailedToLoadPTException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream pTableData = Element.class.getClassLoader().getResourceAsStream(PERIODIC_TABLE_PATH);
            return mapper.readValue(pTableData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new FailedToLoadPTException(PERIODIC_TABLE_PATH + " not found.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Element findElementBySymbol(String symbol) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream pTableData = ElementRepoImpl.class.getClassLoader().getResourceAsStream(PERIODIC_TABLE_PATH);
            List<Element> pt = mapper.readValue(pTableData, new TypeReference<>() {
            });
            // lame efficiency search
            for (Element element : pt) {
                if (element.getSymbol().equalsIgnoreCase(symbol)) {
                    return element;
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
