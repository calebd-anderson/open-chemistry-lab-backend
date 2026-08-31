package chemlab.repository.game.quiz;

import chemlab.model.game.UserQuiz;
import chemlab.model.user.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserQuizRepository extends MongoRepository<UserQuiz, ObjectId> {
    List<UserQuiz> findByUserId(String userId);
}
