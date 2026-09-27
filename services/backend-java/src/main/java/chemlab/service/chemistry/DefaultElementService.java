package chemlab.service.chemistry;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.domain.repository.ElementRepository;
import chemlab.domain.service.chemistry.ElementService;
import chemlab.infrastructure.pubchem.PubChemElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DefaultElementService implements ElementService {

    private final ElementRepository elmRepo;

    public DefaultElementService(ElementRepository elmRepo) {
        this.elmRepo = elmRepo;
    }

    public List<PubChemElement> getAllElements() throws FailedToLoadPTException {
        log.trace("Finding all elements.");
        return elmRepo.findAll();
    }

    // 2. Get item by symbol
    public Optional<PubChemElement> getElementBySymbol(String symbol) {
        return elmRepo.findElementBySymbol(symbol);
    }
}
