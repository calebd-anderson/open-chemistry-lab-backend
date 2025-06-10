package chemlab.controllers.chemistry;

import chemlab.exceptions.domain.PugApiException;
import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.CompoundDTO;
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
    public Reaction validate(@RequestBody CompoundDTO payload) throws PugApiException {
        Reaction reaction = new Reaction(payload.getMappedPayload(), payload.getUserId());
        LOG.info("Controller received formula, userId: {}, {}", reaction.getFormula(), reaction.getUserId());
        return reactionService.validateInput(reaction);
    }

    @GetMapping(value = "getByUserId")
    public List<Reaction> getByUserId(@RequestParam String userId) {
        return reactionService.getCompoundsByUserId(userId);
    }
}
