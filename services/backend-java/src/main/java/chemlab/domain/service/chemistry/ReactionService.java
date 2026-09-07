package chemlab.domain.service.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import chemlab.shared.requests.ReactionRequest;
import chemlab.shared.responses.ReactionResponse;

import java.util.List;

public interface ReactionService {
    boolean hasCompoundBeenDiscovered(String formula);
    ReactionResponse createReaction(ReactionRequest reaction) throws PugApiException;
    Reaction analyzeFormula(Reaction reaction) throws PugApiException;
    List<Reaction> findAllDiscoveredReactions();
    List<UserReaction> getCompoundsByUserId(String userId);
}
