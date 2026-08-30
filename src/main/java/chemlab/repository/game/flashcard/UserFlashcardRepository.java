package chemlab.repository.game.flashcard;

import chemlab.model.game.UserFlashcardDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFlashcardRepository extends MongoRepository<UserFlashcardDocument, String> {
    Page<UserFlashcardDocument> findByUserId(String userId, Pageable pageable);

    long countByUserId(String userId);
}

