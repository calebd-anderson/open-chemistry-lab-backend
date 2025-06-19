package chemlab.service.user;

import chemlab.domain.user.UserReactionService;
import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.UserReaction;
import chemlab.model.user.User;
import chemlab.repository.user.RegisteredUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserReactionServiceImpl implements UserReactionService {

    @Autowired
    RegisteredUserRepository userRepo;

    @Override
    public void saveReactionWithUser(String userId, Reaction reaction) {
        try {
            User user = userRepo.findRegisteredUserByUserId(userId);
            List<UserReaction> userReactions = user.getDiscoveredReactions();
            // skip if reaction already saved with user
            for (UserReaction userReaction : userReactions) {
                if (Objects.equals(userReaction.getUserDiscoveredReaction().getFormula(), reaction.getFormula())) {
                    userReaction.setLastDiscoveredWhen(Instant.now());
                    userRepo.save(user);
                    return;
                }
            }
            UserReaction userReaction = new UserReaction(reaction);
            userReaction.setDiscoveredWhen(Instant.now());
            userReaction.setLastDiscoveredWhen(Instant.now());
            userReactions.add(userReaction);
            userRepo.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<UserReaction> findReactionsByUserId(String userId) {
        User user = userRepo.findRegisteredUserByUserId(userId);
        return user.getDiscoveredReactions();
    }

}
