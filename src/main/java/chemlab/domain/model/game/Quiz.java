package chemlab.domain.model.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@RequiredArgsConstructor
@Document
public class Quiz {
	private ObjectId id;
	private final QuizType quizType;
	private final String formula;
	private final String question;
	private final String answer;
}
