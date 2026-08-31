package chemlab.model.game;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user_quizzes")
public class UserQuiz {
    @Id
    ObjectId id;

    /** user.userId (not the Mongo _id) - kept as String for simpler lookups */
    @Indexed
    private String userId;

    String question;

    String answer;
}
