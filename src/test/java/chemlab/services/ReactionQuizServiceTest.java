package chemlab.services;

import chemlab.domain.game.QuizService;
import chemlab.model.chemistry.Reaction;
import chemlab.model.shared.CreateQuizDto;
import chemlab.repository.chemistry.ElementRepository;
import chemlab.repository.chemistry.ReactionRepository;
import chemlab.repository.user.RegisteredUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionQuizServiceTest {

    @Mock
    private ElementRepository elementRepo;
    @Mock
    private RegisteredUserRepository userRepo;
    @Mock
    private ReactionRepository reactionRepository;
    @Mock
    private QuizService quizService;
//    @InjectMocks
//    private QuizService quizService = new QuizServiceImpl();

    @Test
    @DisplayName("it should insert a new quiz into the repo")
    @WithMockUser(username = "testuser", roles = {"USER", "ADMIN"}, password = "abc123")
    void createQuiz_success() {
        // Arrange
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        // create reaction from elements
        Reaction r1 = new Reaction(elements);
        r1.setTitle("Water");

        String formula = "H2O";

        CreateQuizDto quizDto = new CreateQuizDto(formula, r1.getTitle());

//        String q1 = "What is the name of this compound: " + quizDto.getFormula() + "?";
//        String a1 = quizDto.getReactionName();
//        String q2 = "What is the formula for " + quizDto.getReactionName() + "?";
//        String a2 = quizDto.getFormula();
//
        // Act
        quizService.findQuizByUserId("testuser");

        // Assert
        verify(quizService, atLeastOnce()).findQuizByUserId("testuser");
    }
}
