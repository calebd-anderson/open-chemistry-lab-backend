package chemlab.repository;

import chemlab.domain.model.game.FormulaQuiz;
import chemlab.domain.model.game.QuestionAnswer;
import chemlab.domain.model.game.QuizType;
import chemlab.domain.repository.game.QuizRepository;
import auth.config.CorsProperties;
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
public class FormulaQuizRepositoryTest {
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
        FormulaQuiz fq1 = new FormulaQuiz("abcd");
        fq1.setQuestionAnswerList(List.of(
                new QuestionAnswer("Is this the first still?", "no"),
                new QuestionAnswer("Is this the first?", "yes")
        ));
        FormulaQuiz fq2 = new FormulaQuiz("abcd");
        fq2.setQuestionAnswerList(List.of(
                new QuestionAnswer("non-test", "no"),
                new QuestionAnswer("still-testing", "yes")
        ));

        List<FormulaQuiz> formulaQuizzes = Arrays.asList(fq1, fq2);
        for (FormulaQuiz fq : formulaQuizzes) {
            quizRepo.save(fq);
        }
    }
}
