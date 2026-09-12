package chemlab.infrastructure.persistence.user;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReactionRepository extends MongoRepository<UserReaction, String> {
    List<UserReaction> findByUserId(String userId);
    UserReaction findByUserIdAndReaction(String userId, Reaction reaction);
}
