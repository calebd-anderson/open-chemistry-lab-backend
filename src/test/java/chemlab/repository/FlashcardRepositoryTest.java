package chemlab.repository;

import chemlab.model.game.Flashcard;
import chemlab.repository.game.FlashcardRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
public class FlashcardRepositoryTest {
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0.0"));

    @Autowired
    private FlashcardRepository flashcardRepo;
    @MockitoBean
    private CorsProperties corsProperties;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testdb");
    }

    @BeforeEach
    public void setUp() {
        createMockFlashcards();
    }

    @AfterEach
    void tearDown() {
        flashcardRepo.deleteAll();
    }

    private void createMockFlashcards() {
        Flashcard fc1 = new Flashcard("Is this the first?", "yes");
        Flashcard fc2 = new Flashcard("Is this the first still?", "no");
        Flashcard fc3 = new Flashcard("iS ThIs ThE fIrSt UnIqUe?", "no");
        Flashcard fc4 = new Flashcard("Is this the last?", "yes");
        List<Flashcard> flashcards = Arrays.asList(fc1, fc2, fc3, fc4);
        for (Flashcard flashcard : flashcards) {
            flashcardRepo.save(flashcard);
        }
    }

    @Test
    public void testFindAllFlashcards() {
        List<Flashcard> flashcards = flashcardRepo.findAll();
        assertThat(flashcards).isNotEmpty();
        assertThat(flashcards.size()).isGreaterThan(0);
        System.out.println("Flashcard Data:");
        for (Flashcard flashcard : flashcards) {
            System.out.println("Question: " + flashcard.getQuestion());
            System.out.println("Answer: " + flashcard.getAnswer());
            System.out.println();
        }
    }

    @Test
    @DisplayName("it should return an empty array if findByQuestion query returns no result")
    void test_findByQuestion_return_empty() {
        assertEquals(0, flashcardRepo.findByQuestion("Does this query exist?").size());
    }

    @Test
    @DisplayName("it should query the repo by questions and return a list of flashcards")
    void test_findByQuestion() {
        assertNotNull(flashcardRepo.findByQuestion("Is this the first?"));
    }

    @Test
    @DisplayName("it should return an empty array if findByAnswer query returns no result")
    void test_findByAnswer_return_empty() {
        assertEquals(0, flashcardRepo.findByQuestion("This is an invalid answer.").size());
    }

    @Test
    @DisplayName("it should query the repo by answers and return a list of flashcards")
    void test_findByAnswer() {
        List<Flashcard> flashcards = flashcardRepo.findByAnswer("yes");
        assertNotNull(flashcards);
        assertEquals(2, flashcards.size());
    }

//    @Test
//    @DisplayName("it should find the flashcard by gameId")
//    void test_findByGameId() {
//        List<Flashcard> allFlashcards = flashcardRepo.findAll();
//
//        Flashcard fc = allFlashcards.get(0);
//        Flashcard result = flashcardRepo.findByGameId(fc.getGameId());
//
//        assertEquals(fc.getGameId(), result.getGameId());
//    }
}
