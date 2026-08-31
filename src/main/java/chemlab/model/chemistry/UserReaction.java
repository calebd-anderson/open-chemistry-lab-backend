package chemlab.model.chemistry;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;

/**
 * Top-level document to store per-user discovered reactions.
 * This is a migration target for moving reactions out of the embedded
 * `User.discoveredReactions` array into a dedicated collection.
 */
@Data
@Document(collection = "user_reactions")
public class UserReaction {
    @Id
    private String id;

    /** user.userId (not the Mongo _id) - kept as String for simpler lookups */
    @Indexed
    private String userId;

    @DocumentReference
    private final Reaction reaction;

    private Instant userDiscoveredWhen;

    private Instant userLastDiscoveredWhen;

    private int userDiscoveredCount;
}
