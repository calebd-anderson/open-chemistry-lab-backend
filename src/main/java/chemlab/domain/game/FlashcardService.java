package chemlab.domain.game;

import chemlab.domain.model.game.Flashcard;

import java.util.List;

public interface FlashcardService {
    List<Flashcard> list();

    List<Flashcard> listUserFlashcards(String userId);

    Flashcard create(final Flashcard flashcard);

    Flashcard getFlashcardById(String id);

    List<Flashcard> queryByQuestion(String question);

    List<Flashcard> queryByAnswer(String answer);

    boolean isValid(Flashcard obj);
}
