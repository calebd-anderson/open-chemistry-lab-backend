package chemlab.repository.chemistry;

import chemlab.domain.game.FlashcardService;
import chemlab.domain.game.QuizService;
import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.repository.chemistry.ReactionRepository;
import chemlab.domain.repository.game.FlashcardRepository;
import chemlab.domain.repository.game.QuizRepository;
import chemlab.domain.repository.user.RegisteredUserRepository;
import chemlab.domain.repository.user.UserReactionsRepo;
import auth.config.CorsProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
public class ReactionRepositoryTest {
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0.0"));

    @Autowired
    private ReactionRepository compoundRepo;

    private Reaction c1;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testdb");
//        registry.add("spring.data.mongodb.auto-index-creation", () -> true);
    }

    @BeforeEach
    public void setUp() {
        createMockCompounds();
    }

    @AfterEach
    void tearDown() {
        compoundRepo.deleteAll();
    }

    private void createMockCompounds() {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        c1 = new Reaction(elements);
        compoundRepo.save(c1);
    }

    @Test
    @DisplayName("it should fetch the compound from the repo if it exists")
    void getCompoundByFormula() {
        Reaction result = compoundRepo.findReactionByFormula("H2O");
        assertTrue(result.equals(c1));
    }

// user is null obviously
//    @Test
//    @DisplayName("it should return all quizes from the queried user")
//    void getCompoundsByUserId() {
//        List<Reaction> result = userReactionsRepo.findReactionsByUserId("12345");
//        System.out.println("size: " + result.size());
//        assertEquals(1, result.size());
//    }
}
