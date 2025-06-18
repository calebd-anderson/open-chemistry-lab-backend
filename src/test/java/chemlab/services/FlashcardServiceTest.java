package chemlab.services;

import chemlab.domain.game.FlashcardService;
import chemlab.model.game.Flashcard;
import chemlab.model.user.User;
import chemlab.repository.game.FlashcardRepository;
import chemlab.repository.user.RegisteredUserRepository;
import chemlab.service.game.FlashcardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import shared.FlashcardDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@AutoConfigureMockMvc
class FlashcardServiceTest {
    @Autowired
    private WebApplicationContext context;

    @Mock
    private FlashcardRepository flashcardRepo;
    @Mock
    private RegisteredUserRepository userRepo;
    @Autowired
    private MockMvc mockMvc;
//    @Mock
//    private AuthenticationManager authenticationManager;

    @InjectMocks
    private FlashcardService flashcardService = new FlashcardServiceImpl();

    private final String question1 = "is this mock value 1?";
    private final String question2 = "is this mock value 2?";
    private final String question3 = "is this mock value 3?";
    private final String question4 = "is this mock value 4?";
    private final String answerYes = "yes";
    private final String answerNo = "no";

    @BeforeEach
    void setUp() {

        String question5 = "This might be unique?";

        when(flashcardRepo.findAll()).thenReturn(
                Stream.of(new Flashcard(question1, answerYes),
                                new Flashcard(question2, answerYes),
                                new Flashcard(question3, answerNo),
                                new Flashcard(question4, answerNo),
                                new Flashcard(question5, answerYes))
                        .collect(Collectors.toList()));

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("it should request findAll")
    void test_list() {
        flashcardService.list();
        verify(flashcardRepo, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER", "ADMIN"}, password = "abc123")
    void test_create_success() throws Exception {
        // Arrange
        // configure a user to hold the flashcard
        User user = new User();
        user.setUserId("12345");
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        when(userRepo.findRegisteredUserByUsername("testuser")).thenReturn(user);
        // create a flashcard
        FlashcardDto fc = new FlashcardDto("12345", question1, answerYes);

        ModelMapper modelMapper = new ModelMapper();
        Flashcard newFlashcard = modelMapper.map(fc, Flashcard.class);

        // Act
        List<Flashcard> result = flashcardService.create(fc);
        // Assert
        verify(userRepo, atLeastOnce()).save(user);
        assertTrue(result.contains(newFlashcard));
    }

    @Test
    @DisplayName("it should return the question")
    void test_get_questions() {
        List<Flashcard> returnValue = new ArrayList<>();
        returnValue.add(new Flashcard(question1, answerYes));
        returnValue.add(new Flashcard(question2, answerYes));
        returnValue.add(new Flashcard(question3, answerNo));
        returnValue.add(new Flashcard(question4, answerNo));
        when(flashcardRepo.findByQuestion(question1)).thenReturn(returnValue);

        List<Flashcard> result = flashcardService.queryByQuestion(question1);

        assertNotNull(result);
    }

    @Test
    @DisplayName("it should return false")
    void isValid_returns_false() {
        Flashcard fc = new Flashcard(question1, answerYes);
        boolean result = flashcardService.isValid(fc);
        assertFalse(result);
    }

    @Test
    @DisplayName("it should return true")
    void isValid_returns_true() {
        String question1 = "Is this a unique value?";
        String answer1 = "Maybe";
        Flashcard fc = new Flashcard(question1, answer1);

        assertTrue(flashcardService.isValid(fc));
    }

}
