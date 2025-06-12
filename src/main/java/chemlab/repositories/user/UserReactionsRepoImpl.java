package chemlab.repositories.user;

import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.UserReaction;
import chemlab.model.user.User;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Repository
public class UserReactionsRepoImpl implements UserReactionsRepo {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    RegisteredUserRepository userRepo;

    @Override
    public void saveReactionWithUser(String userId, Reaction reaction) {
        try {
            User user = userRepo.findRegisteredUserByUserId(userId);
            List<UserReaction> userReactions = user.getDiscoveredReactions();
            // skip if reaction already saved with user
            for (UserReaction userReaction : userReactions) {
                if (Objects.equals(userReaction.getFormula(), reaction.getFormula())) {
                    return;
                }
            }
            // map reaction to userReaction
            ModelMapper modelMapper = new ModelMapper();
            UserReaction userReaction = modelMapper.map(reaction, UserReaction.class);
            userReaction.setIDiscoveredWhen(Instant.now());
            userReactions.add(userReaction);
            userRepo.save(user);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public List<UserReaction> findReactionsByUserId(String userId) {
        User user = userRepo.findRegisteredUserByUserId(userId);
        return user.getDiscoveredReactions();
    }
}
