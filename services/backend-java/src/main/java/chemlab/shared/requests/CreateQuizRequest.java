package chemlab.shared.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateQuizRequest {
    public final String formula;
    public final String reactionName;
}
