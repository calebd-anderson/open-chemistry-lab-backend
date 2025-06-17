package chemlab.domain.model.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "quizzes")
@Data
@RequiredArgsConstructor
public class Quiz {
    @Indexed(unique = true)
    @BsonProperty(value = "quiz_type")
    private final QuizType quizType;
    @BsonProperty(value = "formula_quizzes")
    private List<ReactionQuiz> reactionQuizzes;
}
