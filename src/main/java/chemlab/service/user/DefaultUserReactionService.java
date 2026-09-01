package chemlab.service.user;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.UserReaction;
import chemlab.domain.service.user.UserReactionService;
import chemlab.repository.user.UserReactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class DefaultUserReactionService implements UserReactionService {

    @Autowired
    UserReactionRepository userReactionRepo;

    @Override
    public void saveReactionWithUser(String userId, Reaction reaction) {
        try {
            UserReaction userReaction = userReactionRepo.findByUserIdAndReaction(userId, reaction);
            if (userReaction == null) {
                userReaction = new UserReaction(reaction);
                userReaction.setUserId(userId);
                userReaction.setUserDiscoveredWhen(Instant.now());
            }
            userReaction.setUserLastDiscoveredWhen(Instant.now());
            userReaction.setUserDiscoveredCount(userReaction.getUserDiscoveredCount() + 1);
            userReactionRepo.save(userReaction);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<UserReaction> findReactionsByUserId(String userId) {
        return userReactionRepo.findByUserId(userId);
    }
}
