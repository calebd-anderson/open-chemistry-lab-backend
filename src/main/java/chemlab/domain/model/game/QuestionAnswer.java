package chemlab.domain.model.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class QuestionAnswer {
    private final String question;
    private final String answer;
}
