package presentation.controllers.chemistry;

import chemlab.domain.chemistry.ReactionService;
import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.exceptions.domain.PugApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shared.ReactionDto;

import java.util.List;

@RestController
@RequestMapping("/compound")
@Slf4j
public class ReactionController {
    @Autowired
    private ReactionService reactionService;

    @PostMapping(value = "validate")
    public Reaction validate(@RequestBody ReactionDto payload) throws PugApiException {
        Reaction reaction = new Reaction(payload.getMappedPayload());
        log.trace("Controller received formula: {}", reaction.getFormula());
        return reactionService.validateInput(reaction);
    }

    @GetMapping(value = "getByUserId")
    public List<UserReaction> getByUserId(@RequestParam String userId) {
        return reactionService.getCompoundsByUserId(userId);
    }
}
