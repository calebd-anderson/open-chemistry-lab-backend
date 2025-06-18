package chemlab.services;

import chemlab.domain.game.QuizService;
import chemlab.model.chemistry.Reaction;
import chemlab.model.game.ReactionQuiz;
import chemlab.model.game.QuestionAnswer;
import chemlab.domain.repository.chemistry.ElementRepository;
import chemlab.domain.repository.chemistry.ReactionRepository;
import chemlab.domain.repository.game.QuizRepository;
import chemlab.domain.repository.user.RegisteredUserRepository;
import chemlab.service.game.QuizServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shared.CreateQuizDto;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionQuizServiceTest {

    @Mock
    private QuizRepository quizRepo;
    @Mock
    private ElementRepository elementRepo;
    @Mock
    private RegisteredUserRepository userRepo;
    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private QuizService quizService = new QuizServiceImpl();


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
//        // create list of reactions
//        List<Reaction> reactions = new ArrayList<>();
//        reactions.add(r1);

        String formula = "H2O";

        CreateQuizDto quizDto = new CreateQuizDto(formula, r1.getTitle());

        String q1 = "What is the name of this compound: " + quizDto.getFormula() + "?";
        String a1 = quizDto.getReactionName();
        String q2 = "What is the formula for " + quizDto.getReactionName() + "?";
        String a2 = quizDto.getFormula();

        ReactionQuiz reactionQuiz = new ReactionQuiz(r1);
        reactionQuiz.setQuestionAnswerList(List.of(
                new QuestionAnswer(q1, a1),
                new QuestionAnswer(q2, a2)
        ));

//        reactionQuiz.setId(new ObjectId());

        // stub in the repo finds the reaction
//        when(quizRepo.save(isA(Quiz.class))).thenReturn(isA(Quiz.class));

        // Act
        quizService.createQuiz(quizDto);

        // Assert
        verify(quizRepo, atLeastOnce()).createFormulaQuiz(isA(ReactionQuiz.class));
    }
}
