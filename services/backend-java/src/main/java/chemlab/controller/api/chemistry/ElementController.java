package chemlab.controller.api.chemistry;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.domain.service.chemistry.ElementService;
import chemlab.infrastructure.pubchem.PubChemElement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/elements")
public class ElementController {

    private final ElementService elmService;

    ElementController(ElementService elmService)
    {
        this.elmService = elmService;
    }

    @GetMapping(value = "/list")
    public List<PubChemElement> list() throws FailedToLoadPTException {
        return elmService.getAllElements();
    }

    @GetMapping("/symbol/{symbol}")
    public PubChemElement findElementBySymbol(@PathVariable("symbol") String symbol) {
        return elmService.getElementBySymbol(symbol);
    }
}
