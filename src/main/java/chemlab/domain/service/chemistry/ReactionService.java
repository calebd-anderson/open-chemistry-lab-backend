package chemlab.domain.service.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;

import java.util.List;

public interface ReactionService {
    boolean hasCompoundBeenDiscovered(String formula);
    Reaction validateInput(Reaction reaction) throws PugApiException;
    List<Reaction> findAllDiscoveredReactions();
    List<UserReaction> getCompoundsByUserId(String userId);
}
