package chemlab.services;

import chemlab.domain.game.QuizService;
import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.game.Flashcard;
import chemlab.domain.model.game.Quiz;
import chemlab.domain.model.game.QuizType;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.chemistry.ElementRepository;
import chemlab.domain.repository.game.QuizRepository;
import chemlab.domain.repository.user.RegisteredUserRepository;
import chemlab.domain.user.RegisteredUserService;
import chemlab.exceptions.domain.EmailExistException;
import chemlab.exceptions.domain.UserNotFoundException;
import chemlab.exceptions.domain.UsernameExistException;
import chemlab.service.game.QuizServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import shared.CreateQuizDto;
import shared.UserRegisterDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepo;
    @Mock
    private ElementRepository elementRepo;
    @Mock
    private RegisteredUserRepository userRepo;

    @InjectMocks
    private QuizService quizService = new QuizServiceImpl();


    @BeforeEach
    void setUp() throws UserNotFoundException, EmailExistException, UsernameExistException {
//        UserRegisterDto newUser = new UserRegisterDto();
//        newUser.setEmail("bla@mail.com");
//        newUser.setUsername("jimbo");
//        newUser.setPassword("abc123");
//        newUser.setLastName("Aname");
//        newUser.setFirstName("Bname");
//        // I never insert into database yet the user is created ???
//        User user1 = userService.register(newUser);
////        User user = userService.findUserByUsername(newUser.getUsername());
//        globalUser = user1;
    }

//    @AfterEach
//    void tearDown() throws IOException {
//        quizRepo.deleteAll();
//        userService.deleteUser(globalUser.getUsername());
//    }

    @Test
    @DisplayName("it should insert a new quiz into the repo")
    void createQuiz_success() {
        // Arrange
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        // create reaction from elements
        Reaction r1 = new Reaction(elements);
        r1.setTitle("Water");
        // create list of reactions
        List<Reaction> reactions = new ArrayList<>();
        reactions.add(r1);

        String formula = "H2O";

        CreateQuizDto quizDto = new CreateQuizDto(QuizType.COMPOUND, formula, r1.getTitle());

        String q1 = "What is the name of this compound: " + quizDto.getFormula() + "?";
        String a1 = quizDto.getReactionName();
        String q2 = "What is the formula for " + quizDto.getReactionName() + "?";
        String a2 = quizDto.getFormula();

        Quiz quiz1 = new Quiz(quizDto.quizType, quizDto.getFormula(), q1, a1);
        quiz1.setId(new ObjectId());

        // stub in the repo finds the reaction
        when(quizRepo.save(isA(Quiz.class))).thenReturn(quiz1);

        // Act
        quizService.createQuiz(quizDto);

        // Assert
        verify(quizRepo, atLeastOnce()).save(isA(Quiz.class));
    }


    @Test
    @DisplayName("it should request findAll from repo")
    void list() {
        quizService.list();
        verify(quizRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("it should request findQuizByQuizType from repo")
    void findQuizByQuizType() {
        String quizType = "test";
        quizService.findQuizByQuizType(quizType);
        verify(quizRepo, times(1)).findQuizByQuizType(quizType);
    }
}
