package chemlab.service.user;

import chemlab.domain.user.UserReactionService;
import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.UserReaction;
import chemlab.model.chemistry.UserReactionDocument;
import chemlab.model.user.User;
import chemlab.repository.chemistry.ReactionRepository;
import chemlab.repository.chemistry.UserReactionRepository;
import chemlab.repository.user.RegisteredUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserReactionServiceImpl implements UserReactionService {

    @Autowired
    RegisteredUserRepository userRepo;

    @Autowired
    private UserReactionRepository userReactionRepo;

    @Autowired
    private ReactionRepository reactionRepo;

    @Override
    public void saveReactionWithUser(String userId, Reaction reaction) {
        try {
            User user = userRepo.findRegisteredUserByUserId(userId);
            List<UserReaction> userReactions = user.getDiscoveredReactions();
            // skip add if reaction already saved with user
            for (UserReaction userReaction : userReactions) {
                if (Objects.equals(userReaction.getUserDiscoveredReaction().getFormula(), reaction.getFormula())) {
                    userReaction.setUserLastDiscoveredWhen(Instant.now());
                    userReaction.setUserDiscoveredCount(userReaction.getUserDiscoveredCount() + 1);
                    userRepo.save(user);

                    // also update referenced collection
                    UserReactionDocument existingDoc = new UserReactionDocument();
                    existingDoc.setUserId(user.getUserId());
                    if (reaction.getId() != null) {
                        existingDoc.setReactionId(reaction.getId().toHexString());
                    }
                    existingDoc.setUserDiscoveredWhen(userReaction.getUserDiscoveredWhen());
                    existingDoc.setUserLastDiscoveredWhen(userReaction.getUserLastDiscoveredWhen());
                    existingDoc.setUserDiscoveredCount(userReaction.getUserDiscoveredCount());
                    userReactionRepo.save(existingDoc);

                    return;
                }
            }
            UserReaction userReaction = new UserReaction(reaction);
            userReaction.setUserDiscoveredWhen(Instant.now());
            userReaction.setUserLastDiscoveredWhen(Instant.now());
            userReaction.setUserDiscoveredCount(userReaction.getUserDiscoveredCount() + 1);
            userReactions.add(userReaction);
            userRepo.save(user);

            // write to referenced collection as well
            UserReactionDocument doc = new UserReactionDocument();
            doc.setUserId(user.getUserId());
            if (reaction.getId() != null) {
                doc.setReactionId(reaction.getId().toHexString());
            }
            doc.setUserDiscoveredWhen(userReaction.getUserDiscoveredWhen());
            doc.setUserLastDiscoveredWhen(userReaction.getUserLastDiscoveredWhen());
            doc.setUserDiscoveredCount(userReaction.getUserDiscoveredCount());
            userReactionRepo.save(doc);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<UserReaction> findReactionsByUserId(String userId) {
        // Prefer reading from the referenced collection; fallback to embedded list during migration window
        try {
            List<UserReaction> results = new ArrayList<>();
            // fetch referenced documents
            var page = userReactionRepo.findByUserId(userId, org.springframework.data.domain.Pageable.unpaged());
            if (page != null && page.hasContent()) {
                for (UserReactionDocument doc : page.getContent()) {
                    // map to domain UserReaction
                    UserReaction ur = null;
                    if (doc.getReactionId() != null) {
                        String hexId = doc.getReactionId();
                        if (!ObjectId.isValid(hexId)) {
                            throw new IllegalArgumentException("Invalid MongoDB ObjectId: " + hexId);
                        }
                        ObjectId id = new ObjectId(hexId);
                        Optional<Reaction> r = reactionRepo.findById(id);
                        if (r.isPresent()) {
                            ur = new UserReaction(r.get());
                            ur.setUserDiscoveredWhen(doc.getUserDiscoveredWhen());
                            ur.setUserLastDiscoveredWhen(doc.getUserLastDiscoveredWhen());
                            ur.setUserDiscoveredCount(doc.getUserDiscoveredCount());
                        }
                    }
                    if (ur != null) results.add(ur);
                }
                return results;
            }
        } catch (Exception e) {
            log.warn("Error reading from referenced user reactions collection, falling back to embedded reactions", e);
        }

        // fallback to embedded
        User user = userRepo.findRegisteredUserByUserId(userId);
        return user.getDiscoveredReactions();
    }
}
