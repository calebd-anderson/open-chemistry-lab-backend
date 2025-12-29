package chemlab.model.shared;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateQuizDto {
    public final String formula;
    public final String reactionName;
}
