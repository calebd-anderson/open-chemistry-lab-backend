package chemlab.domain.game;

import chemlab.model.game.Flashcard;
import shared.FlashcardDto;

import java.util.List;

public interface FlashcardService {
    List<Flashcard> list();

    List<Flashcard> listUserFlashcards(String userId);

    List<Flashcard> create(FlashcardDto flashcard) throws Exception;

    List<Flashcard> queryByQuestion(String question);

    List<Flashcard> queryByAnswer(String answer);

    boolean isValid(Flashcard obj);
}
