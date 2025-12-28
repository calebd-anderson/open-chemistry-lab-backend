package chemlab.controllers.integration;

import auth.http.JwtAccessDeniedHandler;
import auth.http.JwtAuthenticationEntryPoint;
import auth.jwt.JwtTokenProvider;
import chemlab.domain.game.FlashcardService;
import chemlab.domain.user.RegisteredUserService;
import chemlab.model.game.Flashcard;
import chemlab.repository.chemistry.ReactionRepository;
import chemlab.repository.game.flashcard.FlashcardRepository;
import chemlab.repository.game.quiz.QuizRepository;
import chemlab.repository.user.RegisteredUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import presentation.controllers.api.game.FlashcardController;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FlashcardController.class)
@ExtendWith(SpringExtension.class)
class FlashcardControllerMockTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private MongoTemplate mongoTemplate;
    @MockitoBean
    private FlashcardService flashcardService;
    @MockitoBean
    private ReactionRepository reactionRepository;
    @MockitoBean
    private RegisteredUserService registeredUserService;
    @MockitoBean
    private RegisteredUserRepository registeredUserRepository;
    @MockitoBean
    private QuizRepository quizRepository;
    @MockitoBean
    private FlashcardRepository flashcardRepository;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @MockitoBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockitoBean
    UserDetailsService userDetailsService;
    @MockitoBean
    RestTemplate restTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testList() throws Exception {
        assertNotNull(flashcardService);
        when(flashcardService.list())
                .thenReturn(List.of(new Flashcard("is this correct?", "no")));

        mockMvc.perform(get("/flashcards/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].question").value("is this correct?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].answer").value("no"))
                .andDo(print());
        verify(flashcardService).list();
    }
}
