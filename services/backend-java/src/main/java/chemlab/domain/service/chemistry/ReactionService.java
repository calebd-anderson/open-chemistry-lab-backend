package chemlab.domain.service.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import chemlab.infrastructure.fastapiworker.ClusterMapRequest;
import chemlab.shared.requests.ReactionRequest;
import chemlab.shared.responses.ReactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ReactionService {
    boolean hasCompoundBeenDiscovered(String formula);
    ReactionResponse createReaction(ReactionRequest reaction) throws PugApiException;
    List<ClusterMapRequest> analyzeFormula(ReactionRequest reaction) throws PugApiException, JsonProcessingException;
    List<Reaction> findAllDiscoveredReactions();
    List<UserReaction> getCompoundsByUserId(String userId);
}
