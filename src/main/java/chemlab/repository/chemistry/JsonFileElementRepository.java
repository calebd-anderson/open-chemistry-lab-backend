package chemlab.repository.chemistry;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.infrastructure.pubchem.PubChemElement;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Repository
@Slf4j
public class JsonFileElementRepository implements ElementRepository {

    private final String PERIODIC_TABLE_PATH = "static/data/all_elements.json";

    @Override
    public List<PubChemElement> findAll() throws FailedToLoadPTException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream pTableData = PubChemElement.class.getClassLoader().getResourceAsStream(PERIODIC_TABLE_PATH);
            return mapper.readValue(pTableData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new FailedToLoadPTException(PERIODIC_TABLE_PATH + " not found.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PubChemElement findElementBySymbol(String symbol) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream pTableData = JsonFileElementRepository.class.getClassLoader().getResourceAsStream(PERIODIC_TABLE_PATH);
            List<PubChemElement> pt = mapper.readValue(pTableData, new TypeReference<>() {
            });
            // lame efficiency search
            for (PubChemElement pubChemElement : pt) {
                if (pubChemElement.getSymbol().equalsIgnoreCase(symbol)) {
                    return pubChemElement;
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
