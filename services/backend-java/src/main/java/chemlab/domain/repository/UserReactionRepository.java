package chemlab.domain.repository;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;

import java.util.List;

public interface UserReactionRepository {
    List<UserReaction> findByUserId(String userId);
    UserReaction findByUserIdAndReaction(String userId, Reaction reaction);
    UserReaction save(UserReaction userReaction);
}
