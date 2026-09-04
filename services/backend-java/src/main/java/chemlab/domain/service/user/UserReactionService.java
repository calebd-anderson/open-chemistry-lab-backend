package chemlab.domain.service.user;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;

import java.util.List;

public interface UserReactionService {
    void saveReactionWithUser(String userId, Reaction reaction);
    List<UserReaction> findReactionsByUserId(String userId);
}
