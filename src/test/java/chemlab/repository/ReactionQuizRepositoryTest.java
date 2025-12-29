package chemlab.repository;

import chemlab.auth.config.CorsProperties;
import chemlab.model.chemistry.Reaction;
import chemlab.model.game.QuestionAnswer;
import chemlab.model.game.ReactionQuiz;
import chemlab.repository.game.quiz.QuizRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Testcontainers
@SpringBootTest
public class ReactionQuizRepositoryTest {
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0.0"));

    @Autowired
    private QuizRepository quizRepo;
    @MockitoBean
    private CorsProperties corsProperties;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testdb");
    }

    @BeforeEach
    public void setUp() {
        createMockQuizzes();
    }

    @AfterEach
    void tearDown() {
        quizRepo.deleteAll();
    }

    private void createMockQuizzes() {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        Reaction r1 = new Reaction(elements);

        ReactionQuiz fq1 = new ReactionQuiz(r1);

        fq1.setQuestionAnswerList(List.of(
                new QuestionAnswer("Is this the first still?", "no"),
                new QuestionAnswer("Is this the first?", "yes")
        ));
        ReactionQuiz fq2 = new ReactionQuiz(r1);
        fq2.setQuestionAnswerList(List.of(
                new QuestionAnswer("non-test", "no"),
                new QuestionAnswer("still-testing", "yes")
        ));

        List<ReactionQuiz> reactionQuizzes = Arrays.asList(fq1, fq2);
        for (ReactionQuiz fq : reactionQuizzes) {
            quizRepo.createFormulaQuiz(fq);
        }
    }
}
