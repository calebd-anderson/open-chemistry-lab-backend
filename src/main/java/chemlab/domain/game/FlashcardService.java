package chemlab.domain.game;

import chemlab.model.game.Flashcard;
import chemlab.model.shared.CreateFlashcardRequest;

import java.util.List;

public interface FlashcardService {
    List<Flashcard> listUserFlashcards(String userId);
    List<Flashcard> create(CreateFlashcardRequest flashcard) throws Exception;
}
