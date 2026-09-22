package chemlab.services;

import chemlab.domain.model.game.Flashcard;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.UserRepository;
import chemlab.service.game.DefaultUserFlashcardService;
import chemlab.shared.requests.CreateFlashcardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlashcardServiceTests {
    @Mock
    UserRepository userRepo;
    @InjectMocks
    private DefaultUserFlashcardService flashcardService;

    private List<Flashcard> mockUserFlashcards;

    @BeforeEach
    void setUp() {
        String answerYes = "yes";
        String answerNo = "no";

        mockUserFlashcards = new ArrayList<>(List.of(
                new Flashcard("is this mock value 1?", answerYes),
                new Flashcard("is this mock value 2?", answerNo),
                new Flashcard("is this mock value 3?", answerYes),
                new Flashcard("is this mock value 4?", answerNo)
        ));
    }

    @Test
    void findsUserFlashcards() {
        // Arrange
        String userId = "123456";
        when(userRepo.findByUserId(userId)).thenReturn(Optional.ofNullable(mock(User.class)));
        User user = userRepo.findByUserId(userId).orElseThrow();
        when(user.getUserFlashcards()).thenReturn(mockUserFlashcards);

        // Act
        List<Flashcard> result = flashcardService.listUserFlashcards(userId);

        // Assert
        assertEquals(4, result.size());
    }

    @Test
    void createUserFlashcardSuccess() throws Exception {
        // Arrange
        // create a flashcard request
        CreateFlashcardRequest flashcardRequest = new CreateFlashcardRequest("123456", "is this mock value 1?", "yes");

        // mock the repository to return a user
        User user = new User();
        user.setUserFlashcards(mockUserFlashcards);
        when(userRepo.findByUserId(flashcardRequest.getUserId())).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        int initialNumberOfFlashcards = user.getUserFlashcards().size();

        // Act
        List<Flashcard> result = flashcardService.create(flashcardRequest);

        // Assert
        assertEquals(initialNumberOfFlashcards + 1, result.size());
    }

    @Test
    void deleteUserFlashcardSuccess() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("test_user");
        user.setUserFlashcards(mockUserFlashcards);
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        int initialNumberOfFlashcards = user.getUserFlashcards().size();
        // Act
        flashcardService.delete(user.getUsername(), "is this mock value 1?");
        // Assert
        assertEquals(initialNumberOfFlashcards - 1, user.getUserFlashcards().size());
    }
}
