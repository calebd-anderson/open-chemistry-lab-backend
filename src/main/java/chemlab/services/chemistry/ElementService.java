package chemlab.services.chemistry;

import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.model.chemistry.Element;
import chemlab.repositories.chemistry.ElementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return elmRepo.findAll();
    }

    // 2. Get item by symbol
    public Element getElementBySymbol(String symbol) {
        return elmRepo.findElementBySymbol(symbol);
    }

}
