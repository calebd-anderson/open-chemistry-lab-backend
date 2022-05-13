package edu.metrostate.ics499.team2.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.metrostate.ics499.team2.exceptions.domain.FailedToLoadPTException;
import edu.metrostate.ics499.team2.model.Element;
import edu.metrostate.ics499.team2.repositories.ElementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static edu.metrostate.ics499.team2.constants.FileConstants.PERIODIC_TABLE_PATH;

@Service
@Slf4j
public class ElementService {
    private final ElementRepository elmRepo;

    @Autowired
    public ElementService(ElementRepository elmRepo) {
        this.elmRepo = elmRepo;
    }

    public List<Element> getAllElements() throws FailedToLoadPTException {
        log.info("populating periodic table");
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream pTableData = this.getClass().getClassLoader().getResourceAsStream(PERIODIC_TABLE_PATH);
            return mapper.readValue(pTableData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new FailedToLoadPTException(PERIODIC_TABLE_PATH + " not found.");
        }
    }

    // 2. Get item by symbol
    public Element getElementBySymbol(String symbol) {
        return elmRepo.findElementBySymbol(symbol);
    }

    // 3. Get name and symbol of a all items of a particular family
    public Element getElementByAtomicNumber(String atomicNumber) {
        return elmRepo.getElementByAtomicNumber(atomicNumber);
    }

}
