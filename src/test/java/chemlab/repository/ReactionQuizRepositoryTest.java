package chemlab.repository;

import chemlab.auth.config.CorsProperties;
import chemlab.model.chemistry.Reaction;
import chemlab.model.game.UserQuiz;
import chemlab.repository.game.quiz.UserQuizRepository;
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

import java.util.HashMap;

@Testcontainers
@SpringBootTest
public class ReactionQuizRepositoryTest {
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0.0"));

    @Autowired
    private UserQuizRepository quizRepo;
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

        UserQuiz q1 = new UserQuiz();
        q1.setUserId("test-user-id"); // set the userId
        q1.setQuestion("Is this the first still?");
        q1.setAnswer("no");

        UserQuiz q2 = new UserQuiz();
        q2.setUserId("test-user-id"); // set the userId
        q2.setQuestion("non-test");
        q2.setAnswer("no");

        quizRepo.save(q1);
        quizRepo.save(q2);
    }
}
