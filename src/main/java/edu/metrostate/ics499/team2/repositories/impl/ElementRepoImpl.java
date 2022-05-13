package edu.metrostate.ics499.team2.repositories.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.metrostate.ics499.team2.exceptions.domain.FailedToLoadPTException;
import edu.metrostate.ics499.team2.model.Element;
import edu.metrostate.ics499.team2.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static edu.metrostate.ics499.team2.constants.FileConstants.PERIODIC_TABLE_PATH;

@Repository
public class ElementRepoImpl implements ElementRepository {

    @Override
    public List<Element> findAll() throws FailedToLoadPTException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream pTableData = ElementRepoImpl.class.getClassLoader().getResourceAsStream(PERIODIC_TABLE_PATH);
            return mapper.readValue(pTableData, new TypeReference<>() {
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
            List<Element> pt = mapper.readValue(pTableData, new TypeReference<>() {
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
