package chemlab.controllers.integration;

import auth.jwt.JwtTokenProvider;
import chemlab.domain.game.QuizService;
import chemlab.repository.chemistry.ReactionRepository;
import chemlab.repository.game.flashcard.FlashcardRepository;
import chemlab.repository.game.quiz.QuizRepository;
import chemlab.repository.user.RegisteredUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import presentation.controllers.api.game.QuizController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = QuizController.class)
public class ReactionQuizControllerMockTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

//    @MockitoBean(answers = Answers.RETURNS_MOCKS)
    @MockitoBean
    MongoTemplate mongoTemplate;
    @MockitoBean
    QuizRepository quizRepository;
    @MockitoBean
    private QuizService quizService;
    @MockitoBean
    private RegisteredUserRepository registeredUserRepository;
    @MockitoBean
    private ReactionRepository reactionRepository;
    @MockitoBean
    private FlashcardRepository flashcardRepository;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    RestTemplate restTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

//    @Test
//    void shouldReturnAllQuizzes() throws Exception {
//        // Arrange
//        HashMap<String, Integer> elements = new HashMap<>();
//        elements.put("H", 2);
//        elements.put("O", 1);
//        Reaction r1 = new Reaction(elements);
//
//        ReactionQuiz reactionQuiz = new ReactionQuiz(r1);
//        reactionQuiz.setQuestionAnswerList(List.of(
//                new QuestionAnswer("is this correct?", "no")
//        ));
//        when(quizService.list())
//                .thenReturn(List.of(reactionQuiz));
//
//        // Act
//        this.mockMvc
//                .perform(get("/quiz/all")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding("utf-8"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
//                .andDo(print());
//
//        // Assert
//        verify(quizService).list();
//    }
}
