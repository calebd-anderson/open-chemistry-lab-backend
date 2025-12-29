// the controller class has model view features, end-point config, and error handling
package chemlab.controller.api.chemistry;

import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.model.chemistry.Element;
import chemlab.service.chemistry.ElementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/elements")
public class ElementController {

    @Autowired
    private ElementServiceImpl elmService;

    @GetMapping(value = "/list")
    public List<Element> list() throws FailedToLoadPTException {
        return elmService.getAllElements();
    }

    @GetMapping("/symbol/{symbol}")
    public Element findElementBySymbol(@PathVariable("symbol") String symbol) {
        return elmService.getElementBySymbol(symbol);
    }
}
