package chemlab.model.game;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Per-user flashcard stored in its own collection to avoid embedding large arrays
 * inside the `User` document.
 */
@Data
@Document(collection = "user_flashcards")
public class UserFlashcardDocument {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String question;

    private String answer;

    private Instant createdAt = Instant.now();
}

