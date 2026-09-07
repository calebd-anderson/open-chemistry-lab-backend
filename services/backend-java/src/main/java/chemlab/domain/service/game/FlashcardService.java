package chemlab.domain.service.game;

import chemlab.domain.model.game.Flashcard;
import chemlab.shared.requests.CreateFlashcardRequest;

import java.util.List;

public interface FlashcardService {
    List<Flashcard> listUserFlashcards(String userId);
    List<Flashcard> create(CreateFlashcardRequest flashcard) throws Exception;
}
