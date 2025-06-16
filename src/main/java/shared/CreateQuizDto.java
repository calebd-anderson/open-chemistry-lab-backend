package shared;

import chemlab.domain.model.game.QuizType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateQuizDto {
    public final QuizType quizType;
    public final String formula;
    public final String reactionName;
}
