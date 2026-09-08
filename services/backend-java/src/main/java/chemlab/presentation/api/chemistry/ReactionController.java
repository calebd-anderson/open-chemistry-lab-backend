package chemlab.presentation.api.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.domain.service.chemistry.ReactionService;
import chemlab.domain.service.ml.UnsupervisedClustMap;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import chemlab.infrastructure.fastapiworker.ClusterMapRequest;
import chemlab.shared.requests.ReactionRequest;
import chemlab.shared.responses.ClusterMapResponse;
import chemlab.shared.responses.ReactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compound")
@Slf4j
public class ReactionController {
    @Autowired
    private ReactionService reactionService;
    @Autowired
    private UnsupervisedClustMap unsupervisedClustMap;

    @PostMapping(value = "validate")
    public ReactionResponse validate(@RequestBody ReactionRequest payload) throws PugApiException {
        log.trace("Controller received formula: {}", payload.getElements());
        return reactionService.createReaction(payload);
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
    public ClusterMapResponse analyzeReaction(@RequestBody ReactionRequest payload) throws PugApiException, JsonProcessingException {
        List<ClusterMapRequest> data = reactionService.analyzeFormula(payload);
        ClusterMapResponse response = unsupervisedClustMap.testMl(data);
        return response;
    }
}
