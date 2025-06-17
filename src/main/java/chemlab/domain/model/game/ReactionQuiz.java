package chemlab.domain.model.game;

import chemlab.domain.model.chemistry.Reaction;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ReactionQuiz {
    @DocumentReference
    private final Reaction reaction;
    private List<QuestionAnswer> questionAnswerList;
}
