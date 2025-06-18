// the controller class has model view features, end-point config, and error handling
package presentation.controllers.chemistry;

import chemlab.exceptions.domain.FailedToLoadPTException;
import chemlab.model.chemistry.Element;
import chemlab.service.chemistry.ElementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

// thymeleaf doesn't work with @RestController (see url below)
// https://stackoverflow.com/questions/63965406/unable-to-render-thymleaf-page-from-spring-boot-controller-prints-only-the-retu
@Controller
@RequestMapping("/elements")
public class ElementController {

    private final ElementServiceImpl elmService;

    @Autowired
    public ElementController(ElementServiceImpl service) {
        this.elmService = service;
    }

    // thymeleaf table template of all elements
    @GetMapping
    public String getElements(Model model) throws FailedToLoadPTException {
        model.addAttribute("elms", list());
        return "elements";
    }

    @GetMapping(value = "/list", produces = "application/json")
    @ResponseBody
    public List<Element> list() throws FailedToLoadPTException {
        return elmService.getAllElements();
    }

    @GetMapping("/symbol/{symbol}")
    @ResponseBody
    public Element findElementBySymbol(@PathVariable("symbol") String symbol) {
        return elmService.getElementBySymbol(symbol);
    }

}
