package chemlab.controllers;

import chemlab.auth.http.JwtAccessDeniedHandler;
import chemlab.auth.http.JwtAuthenticationEntryPoint;
import chemlab.auth.jwt.JwtTokenProvider;
import chemlab.controller.api.chemistry.ReactionController;
import chemlab.domain.game.QuizService;
import chemlab.domain.user.RegisteredUserService;
import chemlab.repository.chemistry.ReactionRepository;
import chemlab.repository.game.flashcard.FlashcardRepository;
import chemlab.repository.game.quiz.QuizRepository;
import chemlab.repository.user.RegisteredUserRepository;
import chemlab.service.chemistry.ReactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ReactionController.class)
class ReactionControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private MongoTemplate mongoTemplate;
    @MockitoBean
    private ReactionRepository reactionRepository;
    @MockitoBean
    private ReactionServiceImpl reactionServiceMock;
    @MockitoBean
    private FlashcardRepository flashcardRepository;
    @MockitoBean
    private QuizRepository quizRepository;
    @MockitoBean
    private RegisteredUserRepository registeredUserRepository;
    @MockitoBean
    private RegisteredUserService registeredUserService;
    @MockitoBean
    private QuizService quizService;

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
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
//                .apply(springSecurity())
                .build();
    }

    String responseBody = "{\n"
            + "    \"PropertyTable\": {\n"
            + "        \"Properties\": [\n"
            + "            {\n"
            + "                \"CID\": 5234,\n"
            + "                \"MolecularFormula\": \"ClNa\",\n"
            + "                \"MolecularWeight\": \"58.44\",\n"
            + "                \"Title\": \"Sodium chloride\"\n"
            + "            },\n"
            + "            {\n"
            + "                \"CID\": 23667643,\n"
            + "                \"MolecularFormula\": \"ClNa\",\n"
            + "                \"MolecularWeight\": \"57.45\",\n"
            + "                \"Title\": \"Sodium chloride na-22\"\n"
            + "            }\n"
            + "        ]\n"
            + "    }\n"
            + "}";

    String responseBodyFail = "{}";

    @Test
    @DisplayName("Test validation of payload from user")
    void test_process() {
    }
}