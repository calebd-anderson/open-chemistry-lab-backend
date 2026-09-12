package chemlab.service.chemistry;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.domain.repository.ElementRepository;
import chemlab.domain.service.chemistry.ElementService;
import chemlab.infrastructure.pubchem.PubChemElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DefaultElementService implements ElementService {

//    @Autowired
    private final chemlab.domain.repository.ElementRepository elmRepo;

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
