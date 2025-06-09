package chemlab.repositories;

import chemlab.model.game.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {
	List<Quiz> findByQuestion(String question);
	List<Quiz> findByAnswer(String Answer);

	@Query("{ userId: '?0' }")
	List<Quiz> findQuizByUserId(String userId);

	@Query("{ quizType: '?0' }")
	List<Quiz> findQuizByQuizType(String quizType);
}
