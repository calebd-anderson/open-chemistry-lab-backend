package chemlab.repository.chemistry;

import chemlab.model.chemistry.Reaction;
import chemlab.repositories.chemistry.ReactionRepository;
import chemlab.security.config.CorsProperties;
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
        String userId = "12345";
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        c1 = new Reaction(elements, userId);
        compoundRepo.save(c1);
    }

    @Test
    @DisplayName("it should fetch the compound from the repo if it exists")
    void getCompoundByFormula() {
        List<Reaction> result = compoundRepo.findCompoundByFormula("H2O");
        assertTrue(result.get(0).equals(c1));
    }

    @Test
    @DisplayName("it should return all quizes from the queried user")
    void getCompoundsByUserId() {
        List<Reaction> result = compoundRepo.findCompoundByUserId("12345");
        System.out.println("size: " + result.size());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("it should only return quizes fro the specified user")
    void getCompoundsByUserId_2() {
        String userId = "12345";
        HashMap<String, Integer> elements2 = new HashMap<>();
        elements2.put("H", 1);
        elements2.put("O", 2);

        Reaction c2 = new Reaction(elements2, userId);

        HashMap<String, Integer> elements3 = new HashMap<>();
        elements2.put("Na", 1);
        elements2.put("Cl", 1);

        Reaction c3 = new Reaction(elements2, userId);

        HashMap<String, Integer> elements4 = new HashMap<>();
        elements4.put("H", 1);
        elements4.put("Cl", 1);

        Reaction c4 = new Reaction(elements2, "54321");

        HashMap<String, Integer> elements5 = new HashMap<>();
        elements5.put("C", 1);
        elements5.put("O", 2);

        Reaction c5 = new Reaction(elements2, "54321");

        compoundRepo.save(c2);
        compoundRepo.save(c3);
        compoundRepo.save(c4);
        compoundRepo.save(c5);
        List<Reaction> result = compoundRepo.findCompoundByUserId("12345");
        System.out.println("size: " + result.size());
        assertEquals(3, result.size());
    }
}
