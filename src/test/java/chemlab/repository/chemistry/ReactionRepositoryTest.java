package chemlab.repository.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.repository.chemistry.ReactionRepository;
import chemlab.domain.repository.user.UserReactionsRepo;
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
    @Autowired
    private UserReactionsRepo userReactionsRepo;
    @MockitoBean
    private CorsProperties corsProperties;
    private Reaction c1;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testdb");
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
        List<Reaction> result = compoundRepo.findCompoundByFormula("H2O");
        assertTrue(result.get(0).equals(c1));
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
