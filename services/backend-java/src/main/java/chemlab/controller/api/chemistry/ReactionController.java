package chemlab.controller.api.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.domain.service.chemistry.ReactionService;
import chemlab.domain.service.ml.UnsupervisedClustMap;
import chemlab.infrastructure.fastapiworker.ClusterMapRequest;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import chemlab.shared.requests.ReactionRequest;
import chemlab.shared.responses.ClusterMapResponse;
import chemlab.shared.responses.ReactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/compound")
@Slf4j
public class ReactionController {
    private final ReactionService reactionService;
    private final UnsupervisedClustMap unsupervisedClustMap;

    ReactionController(ReactionService reactionService,  UnsupervisedClustMap unsupervisedClustMap) {
        this.reactionService = reactionService;
        this.unsupervisedClustMap = unsupervisedClustMap;
    }

    @PostMapping(value = "validate")
    public ReactionResponse validate(@RequestBody ReactionRequest payload, Authentication auth) throws PugApiException {
        log.trace("Controller received formula: {}", payload.getElements());
        // Use Optional.ofNullable to wrap the username
        Optional<String> username = (auth != null && auth.isAuthenticated())
                ? Optional.of(auth.getName())
                : Optional.empty();

        return reactionService.createReaction(payload, username);
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

    @PostMapping(value = "analyze")
    public ClusterMapResponse analyzeReaction(@RequestBody ReactionRequest reactionRequest) throws PugApiException, JsonProcessingException {
        log.trace("Analyzing received formula: {}, with Python worker.", reactionRequest.getElements());
        List<ClusterMapRequest> data = reactionService.analyzeFormula(reactionRequest);
        log.trace("Sending FastAPI data to unsupervised learning.");
        return unsupervisedClustMap.testMl(data);
    }
}
