package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.domain.model.chemistry.Element;
import chemlab.domain.repository.chemistry.ElementRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static chemlab.constants.FileConstants.PERIODIC_TABLE_PATH;

@Repository
public class ElementRepoImpl implements ElementRepository {

    @Override
    public List<Element> findAll() throws FailedToLoadPTException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream pTableData = ElementRepoImpl.class.getClassLoader().getResourceAsStream(PERIODIC_TABLE_PATH);
            return mapper.readValue(pTableData, new TypeReference<List<Element>>() {
            });
        } catch (IOException e) {
            throw new FailedToLoadPTException(PERIODIC_TABLE_PATH + " not found.");
        }
    }

    @Override
    public Element findElementBySymbol(String symbol) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream pTableData = ElementRepoImpl.class.getClassLoader().getResourceAsStream(PERIODIC_TABLE_PATH);
            List<Element> pt = mapper.readValue(pTableData, new TypeReference<List<Element>>() {
            });
            // lame efficiency search
            for (Element element : pt) {
                if (element.getSymbol().equalsIgnoreCase(symbol)) {
                    return element;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
