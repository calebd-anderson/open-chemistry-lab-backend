package chemlab.domain;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.exceptions.domain.PugApiException;

import java.util.List;

public interface ReactionService {
    boolean hasCompoundBeenDiscovered(String formula);
    Reaction validateInput(Reaction reaction) throws PugApiException;
    List<UserReaction> getCompoundsByUserId(String userId);
}
