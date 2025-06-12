package chemlab.repository;

import chemlab.domain.model.game.Quiz;
import chemlab.domain.repository.game.QuizRepository;
import auth.CorsProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
public class QuizRepositoryTest {
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
        Quiz q1 = new Quiz("12345", "Is this the first?", "yes", "test");
        Quiz q2 = new Quiz("12345", "Is this the first still?", "no", "test");
        Quiz q3 = new Quiz("54321", "iS ThIs ThE fIrSt UnIqUe?", "no", "non-test");
        Quiz q4 = new Quiz("6789", "Is this the last?", "yes", "still-testing");

        List<Quiz> quizzes = Arrays.asList(q1, q2, q3, q4);
        for (Quiz quiz : quizzes) {
            quizRepo.save(quiz);
        }
    }

    @Test
    @DisplayName("it should return an empty array if findByQuestion query returns no result")
    void test_findByQuestion_return_empty() {
        assertEquals(0, quizRepo.findByQuestion("Does this query exist?").size());
    }

    @Test
    @DisplayName("it should query the repo by questions and return a list of flashcards")
    void test_findByQuestion() {
        assertNotNull(quizRepo.findByQuestion("Is this the first?"));
    }

    @Test
    @DisplayName("it should return an empty array if findByAnswer query returns no result")
    void test_findByAnswer_return_empty() {
        assertEquals(0, quizRepo.findByQuestion("This is an invalid answer.").size());
    }

    @Test
    @DisplayName("it should query the repo by answers and return a list of flashcards")
    void test_findByAnswer() {
        assertNotNull(quizRepo.findByAnswer("yes"));
        assertEquals(2, quizRepo.findByAnswer("yes").size());
    }

    @Test
    @DisplayName("it should return a list of quizes from a specific user")
    void test_findQuizByUserId() {
        List<Quiz> result = quizRepo.findQuizByUserId("12345");
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("it should return a list of quizes by type")
    void test_findQuizByQuizType() {
        List<Quiz> result = quizRepo.findQuizByQuizType("test");
        assertEquals(2, result.size());
    }
}
