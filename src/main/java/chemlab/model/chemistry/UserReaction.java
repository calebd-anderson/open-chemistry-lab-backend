package chemlab.model.chemistry;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;

@Data
public class UserReaction {
    @DocumentReference
    private final Reaction userDiscoveredReaction;
    private Instant userDiscoveredWhen = Instant.EPOCH;
    private Instant userLastDiscoveredWhen = Instant.EPOCH;
    private int userDiscoveredCount;
}
