package chemlab.repositories.user;

import chemlab.model.chemistry.Reaction;
import chemlab.model.user.User;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserReactionsRepoImpl implements UserReactionsRepo {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    RegisteredUserRepository userRepo;

    @Override
    public void saveReactionWithUser(String userId, Reaction reaction) {
        try {
            User user = userRepo.findRegisteredUserByUserId(userId);
            List<Reaction> userReactions = user.getDiscoveredReactions();
            if (userReactions.contains(reaction))
                return;
            userReactions.add(reaction);
            userRepo.save(user);
        } catch (MongoException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public List<Reaction> findReactionsByUserId(String userId) {
        User user = userRepo.findRegisteredUserByUserId(userId);
        return user.getDiscoveredReactions();
    }
}
