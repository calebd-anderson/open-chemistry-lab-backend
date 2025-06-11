package chemlab.model.chemistry;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

// https://stackoverflow.com/questions/56583423/generating-equals-hashcode-implementation-but-without-a-call-to-superclass-even
@Data
@EqualsAndHashCode(callSuper = true)
public class UserReaction extends Reaction {
    public Instant dateIDiscoveredIt;
}
