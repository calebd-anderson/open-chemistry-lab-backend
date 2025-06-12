package chemlab.model.chemistry;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserReaction extends Reaction {
    public Instant iDiscoveredWhen = Instant.EPOCH;
}
