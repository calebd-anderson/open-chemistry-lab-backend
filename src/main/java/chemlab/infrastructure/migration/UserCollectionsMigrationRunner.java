package chemlab.infrastructure.migration;

import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.UserReaction;
import chemlab.model.chemistry.UserReactionDocument;
import chemlab.model.game.Flashcard;
import chemlab.model.game.UserFlashcardDocument;
import chemlab.model.user.User;
import chemlab.repository.chemistry.UserReactionRepository;
import chemlab.repository.game.flashcard.UserFlashcardRepository;
import chemlab.repository.user.RegisteredUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCollectionsMigrationRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(UserCollectionsMigrationRunner.class);

    private final RegisteredUserRepository userRepo;
    private final UserReactionRepository userReactionRepo;
    private final UserFlashcardRepository userFlashcardRepo;

    public UserCollectionsMigrationRunner(RegisteredUserRepository userRepo,
                                          UserReactionRepository userReactionRepo,
                                          UserFlashcardRepository userFlashcardRepo) {
        this.userRepo = userRepo;
        this.userReactionRepo = userReactionRepo;
        this.userFlashcardRepo = userFlashcardRepo;
    }

    @Override
    public void run(String... args) {
        try {
            long existingReactions = userReactionRepo.count();
            long existingFlashcards = userFlashcardRepo.count();
            if (existingReactions > 0 || existingFlashcards > 0) {
                log.info("UserCollectionsMigrationRunner: target collections already contain data (reactions={}, flashcards={}), skipping backfill.", existingReactions, existingFlashcards);
                return;
            }

            List<User> users = userRepo.findAll();
            log.info("UserCollectionsMigrationRunner: found {} users to migrate", users.size());

            int reactionsWritten = 0;
            int flashcardsWritten = 0;

            for (User user : users) {
                String userId = user.getUserId();
                if (user.getDiscoveredReactions() != null) {
                    for (UserReaction ur : user.getDiscoveredReactions()) {
                        UserReactionDocument doc = new UserReactionDocument();
                        doc.setUserId(userId);
                        Reaction reaction = ur.getUserDiscoveredReaction();
                        if (reaction != null && reaction.getId() != null) {
                            doc.setReactionId(reaction.getId().toHexString());
                        }
                        doc.setUserDiscoveredWhen(ur.getUserDiscoveredWhen());
                        doc.setUserLastDiscoveredWhen(ur.getUserLastDiscoveredWhen());
                        doc.setUserDiscoveredCount(ur.getUserDiscoveredCount());
                        userReactionRepo.save(doc);
                        reactionsWritten++;
                    }
                }

                if (user.getUserFlashcards() != null) {
                    for (Flashcard fc : user.getUserFlashcards()) {
                        UserFlashcardDocument doc = new UserFlashcardDocument();
                        doc.setUserId(userId);
                        doc.setQuestion(fc.getQuestion());
                        doc.setAnswer(fc.getAnswer());
                        userFlashcardRepo.save(doc);
                        flashcardsWritten++;
                    }
                }
            }

            log.info("UserCollectionsMigrationRunner: migration complete. reactionsWritten={}, flashcardsWritten={}", reactionsWritten, flashcardsWritten);
        } catch (Exception e) {
            log.error("UserCollectionsMigrationRunner: migration failed", e);
        }
    }
}

