package chemlab.repositories.user;

import chemlab.model.chemistry.Reaction;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserReactionsRepo {
    void saveReactionWithUser(String userId, Reaction reaction);
    List<Reaction> findReactionsByUserId(String userId);
}
