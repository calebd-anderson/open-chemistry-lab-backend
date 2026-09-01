package chemlab.service.chemistry;

import chemlab.domain.chemistry.ElementService;
import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.infrastructure.pubchem.PubChemElement;
import chemlab.repository.chemistry.ElementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DefaultElementService implements ElementService {

//    @Autowired
    private final ElementRepository elmRepo;

    public DefaultElementService(ElementRepository elmRepo) {
        this.elmRepo = elmRepo;
    }

    public List<PubChemElement> getAllElements() throws FailedToLoadPTException {
        log.trace("populating periodic table");
        return elmRepo.findAll();
    }

    // 2. Get item by symbol
    public PubChemElement getElementBySymbol(String symbol) {
        return elmRepo.findElementBySymbol(symbol);
    }
}
