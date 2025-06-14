package chemlab.service.chemistry;

import chemlab.domain.chemistry.ElementService;
import chemlab.domain.model.chemistry.Element;
import chemlab.domain.repository.chemistry.ElementRepository;
import chemlab.exceptions.domain.FailedToLoadPTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ElementServiceImpl implements ElementService {

    private final ElementRepository elmRepo;

    @Autowired
    public ElementServiceImpl(ElementRepository elmRepo) {
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
