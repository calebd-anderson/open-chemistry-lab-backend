package chemlab.domain.model.chemistry;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;

@Data
public class UserReaction {
    @DocumentReference
    private final Reaction userDiscoveredReaction;
    private Instant firstDiscovered = Instant.EPOCH;
    private Instant lastDiscovered = Instant.EPOCH;
}
