package chemlab.controllers.integration;

import chemlab.domain.repository.chemistry.ReactionRepository;
import chemlab.domain.repository.game.FlashcardRepository;
import chemlab.domain.repository.user.RegisteredUserRepository;
import presentation.controllers.game.QuizController;
import chemlab.domain.model.game.Quiz;
import auth.jwt.JwtTokenProvider;
import chemlab.service.game.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = QuizController.class)
public class QuizControllerMockTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private QuizService quizServiceMock;
    @MockitoBean
    private RegisteredUserRepository registeredUserRepository;
    @MockitoBean
    private ReactionRepository reactionRepository;
    @MockitoBean
    private FlashcardRepository flashcardRepository;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    RestTemplateBuilder restTemplateBuilder;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void shouldReturnAllQuizzes() throws Exception {
        when(quizServiceMock.list())
                .thenReturn(List.of(new Quiz("is this correct?", "no")));
        this.mockMvc
                .perform(get("/quiz/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].question").value("is this correct?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].answer").value("no"))
                .andDo(print());
        verify(quizServiceMock).list();
    }
}
