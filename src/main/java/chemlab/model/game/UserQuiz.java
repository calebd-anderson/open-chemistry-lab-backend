package chemlab.model.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserQuiz {
    final String question;
    final String answer;
}
