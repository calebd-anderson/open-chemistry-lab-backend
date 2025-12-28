package chemlab.controllers;

import chemlab.domain.game.FlashcardService;
import chemlab.model.game.Flashcard;
import chemlab.model.user.User;
import chemlab.repository.game.flashcard.FlashcardRepository;
import chemlab.repository.user.RegisteredUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import presentation.controllers.api.game.FlashcardController;
import shared.FlashcardDto;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
//@ExtendWith(SpringExtension.class)
@SpringBootTest
//@AutoConfigureMockMvc
//@WebMvcTest(FlashcardController.class)
class FlashcardControllerTest {
//    @Autowired
//    private WebApplicationContext webApplicationContext;

    @Autowired
    private FlashcardController flashcardController;
    @Autowired
    private FlashcardService flashcardService;
    // mocked in beforeeach
    private FlashcardRepository flashcardRepo;
    @MockitoBean
    private RegisteredUserRepository userRepository;

    @Captor
    ArgumentCaptor<Flashcard> valueCaptor;
    ArgumentCaptor<User> valueCaptor2;

    private final String question = "is this unique?";
    private final String answer = "yes";
    private final FlashcardDto flashcardDto = new FlashcardDto("12345", question, answer);

    @BeforeEach
    void setUp() {
        flashcardRepo = mock(FlashcardRepository.class);
        // manually inject flashcardService repo dependency -> repoMock
        ReflectionTestUtils.setField(flashcardService, "flashcardRepo", flashcardRepo);
    }

    @Test
    @DisplayName("It should instantiate the controller")
    void testInit() {
        assertNotNull(flashcardController);
    }

    @Test
    @DisplayName("It should insert the flashcard into the db")
    @WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
    void testCreate() throws Exception {
        // Arrange
        String question1 = "Make me unique?";
        String answer1 = "yes";
        FlashcardDto flashcardDto2 = new FlashcardDto("12345", question1, answer1);
        Flashcard fc = new Flashcard(question1, answer1);

        doReturn(new ArrayList<Flashcard>()).when(flashcardRepo).findAll();
        doReturn(fc).when(flashcardRepo).save(fc);

        User user = new User();
        user.setUserId("12345");
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        when(userRepository.findRegisteredUserByUsername("testuser")).thenReturn(user);

        // Act
        flashcardController.create(flashcardDto2);

        // Assert
//        verify(flashcardRepo, times(1)).save(valueCaptor.capture());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("It should request findByQuestion when queryQuestion called")
    void testQueryQuestion() {
        flashcardController.queryQuestions(question);
        verify(flashcardRepo, times(1)).findByQuestion(question);
    }

    @Test
    @DisplayName("It should return a list of questions from the db")
    void testQueryAnswers() {
        flashcardController.queryAnswers(answer);
        verify(flashcardRepo, times(1)).findByAnswer(answer);
    }
}
