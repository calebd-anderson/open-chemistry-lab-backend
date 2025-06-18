package chemlab.domain.chemistry;

import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.UserReaction;
import infrastructure.pubchem.exceptions.PugApiException;

import java.util.List;

public interface ReactionService {
    boolean hasCompoundBeenDiscovered(String formula);
    Reaction validateInput(Reaction reaction) throws PugApiException;
    List<UserReaction> getCompoundsByUserId(String userId);
}
