// the controller class has model view features, end-point config, and error handling
package chemlab.presentation.view;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.service.chemistry.DefaultElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// thymeleaf doesn't work with @RestController (see url below)
// https://stackoverflow.com/questions/63965406/unable-to-render-thymleaf-page-from-spring-boot-controller-prints-only-the-retu
@Controller
@RequestMapping("/elements")
public class ElementViewController {

    private final DefaultElementService elmService;

    @Autowired
    public ElementViewController(DefaultElementService service) {
        this.elmService = service;
    }

    // thymeleaf table template of all elements
    @GetMapping
    public String getElements(Model model) throws FailedToLoadPTException {
        model.addAttribute("elms",  elmService.getAllElements());
        return "elements";
    }

}
