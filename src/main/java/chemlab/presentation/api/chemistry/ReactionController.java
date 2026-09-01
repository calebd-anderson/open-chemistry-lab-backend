package chemlab.presentation.api.chemistry;

import chemlab.domain.service.chemistry.ReactionService;
import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import chemlab.shared.ReactionRequest;

import java.util.List;

@RestController
@RequestMapping("/compound")
@Slf4j
public class ReactionController {
    @Autowired
    private ReactionService reactionService;

    @PostMapping(value = "validate")
    public Reaction validate(@RequestBody ReactionRequest payload) throws PugApiException {
        Reaction reaction = new Reaction(payload.getMappedPayload());
        log.trace("Controller received formula: {}", reaction.getFormula());
        return reactionService.validateInput(reaction);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping(value = "getAllDiscoveries")
    public List<Reaction> getAllDiscoveredReactions() {
        return reactionService.findAllDiscoveredReactions();
    }

    @GetMapping(value = "getByUserId")
    public List<UserReaction> getByUserId(@RequestParam String userId) {
        return reactionService.getCompoundsByUserId(userId);
    }
}
