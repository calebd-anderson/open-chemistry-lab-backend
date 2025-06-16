package chemlab.domain.repository.game;

import chemlab.domain.model.game.Flashcard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends MongoRepository<Flashcard, String> {
    @Query("{ 'question' : { $regex: ?0 } }")
    List<Flashcard> findByQuestion(String question);

    @Query("{ 'answer' : { $regex: ?0 } }")
    List<Flashcard> findByAnswer(String answer);
}
