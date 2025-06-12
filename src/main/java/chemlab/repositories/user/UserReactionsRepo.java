package chemlab.repositories.user;

import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.UserReaction;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserReactionsRepo {
    void saveReactionWithUser(String userId, Reaction reaction);
    List<UserReaction> findReactionsByUserId(String userId);
}
