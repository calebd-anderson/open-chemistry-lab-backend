package chemlab.services;

import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.model.game.Flashcard;
import chemlab.domain.model.game.FlashcardMapper;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.UserRepository;
import chemlab.service.game.DefaultUserFlashcardService;
import chemlab.shared.requests.CreateFlashcardRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashcardServiceTests {
    @Mock
    private UserRepository userRepo;
    @Mock
    private FlashcardMapper flashcardMapper;
    @InjectMocks
    private DefaultUserFlashcardService flashcardService;

    @Test
    void listsFlashcardsForUser() {
        String userId = "123456";
        User user = new User("test_user");
        List<Flashcard> flashcards = new ArrayList<>(List.of(
                new Flashcard("Question 1?", "Answer 1"),
                new Flashcard("Question 2?", "Answer 2")
        ));
        user.setUserFlashcards(flashcards);
        when(userRepo.findByUserId(userId)).thenReturn(Optional.of(user));

        List<Flashcard> result = flashcardService.listUserFlashcards(userId);

        assertSame(flashcards, result);
        assertEquals(2, result.size());
        verify(userRepo).findByUserId(userId);
    }

    @Test
    void createsFlashcardAndSavesUpdatedUser() throws Exception {
        String userId = "123456";
        CreateFlashcardRequest request = new CreateFlashcardRequest(userId, "Question?", "Answer");
        Flashcard newFlashcard = new Flashcard("Question?", "Answer");
        User user = new User("test_user");
        user.setUserFlashcards(new ArrayList<>(List.of(new Flashcard("Existing?", "Existing answer"))));
        when(flashcardMapper.toEntity(request)).thenReturn(newFlashcard);
        when(userRepo.findByUserId(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);

        List<Flashcard> result = flashcardService.create(request);

        assertEquals(2, result.size());
        assertSame(newFlashcard, result.get(1));
        verify(flashcardMapper).toEntity(request);
        verify(userRepo).findByUserId(userId);
        verify(userRepo).save(user);
    }

    @Test
    void deletesFlashcardMatchingQuestionAndSavesUpdatedUser() throws Exception {
        String username = "test_user";
        User user = new User(username);
        user.setUserFlashcards(new ArrayList<>(List.of(
                new Flashcard("Delete me?", "Answer"),
                new Flashcard("Keep me?", "Answer")
        )));
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);

        flashcardService.delete(username, "Delete me?");

        assertEquals(List.of(new Flashcard("Keep me?", "Answer")), user.getUserFlashcards());
        verify(userRepo).save(user);
    }

    @Test
    void deletingFlashcardForMissingUserThrowsAndDoesNotSave() {
        String username = "missing_user";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> flashcardService.delete(username, "Question?"));

        verify(userRepo).findByUsername(username);
        verifyNoInteractions(flashcardMapper);
    }
}
