package chemlab.infrastructure.persistence.user;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.domain.repository.UserReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserReactionPersistenceAdapter implements UserReactionRepository {
    @Autowired
    private final SpringDataUserReactionRepository mongoRepository;

    @Override
    public List<UserReaction> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId);
    }

    @Override
    public UserReaction findByUserIdAndReaction(String userId, Reaction reaction) {
        return mongoRepository.findByUserIdAndReaction(userId, reaction);
    }

    @Override
    public UserReaction save(UserReaction userReaction) {
        return mongoRepository.save(userReaction);
    }
}
