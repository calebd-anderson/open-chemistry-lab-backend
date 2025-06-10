package chemlab.controllers.chemistry;

import chemlab.exceptions.domain.PugApiException;
import chemlab.model.Compound;
import chemlab.model.CompoundDTO;
import chemlab.services.chemistry.ReactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compound")
public class ReactionController {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReactionService reactionService;

    @PostMapping(value = "validate")
    public Compound validate(@RequestBody CompoundDTO payload) throws PugApiException {
        Compound compound = new Compound(payload.getMappedPayload(), payload.getUserId());
        LOG.info("Controller received formula, userId: {}, {}", compound.getFormula(), compound.getUserId());
        return reactionService.validateInput(compound);
    }

    @GetMapping(value = "getByUserId")
    public List<Compound> getByUserId(@RequestParam String userId) {
        return reactionService.getCompoundsByUserId(userId);
    }
}
