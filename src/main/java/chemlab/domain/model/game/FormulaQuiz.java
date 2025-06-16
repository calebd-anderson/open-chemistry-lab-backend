package chemlab.domain.model.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@RequiredArgsConstructor
@Document
public class FormulaQuiz {
	@MongoId
	private ObjectId id;
	private final String formula;
	private List<QuestionAnswer> questionAnswerList;
}
