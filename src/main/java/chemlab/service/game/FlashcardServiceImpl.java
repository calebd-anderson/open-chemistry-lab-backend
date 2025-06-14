package chemlab.service.game;

import chemlab.domain.ServiceInterface;
import chemlab.domain.game.FlashcardService;
import chemlab.domain.model.game.Flashcard;
import chemlab.domain.repository.game.FlashcardRepository;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlashcardServiceImpl implements ServiceInterface<Flashcard>, FlashcardService {

    @Autowired
    private FlashcardRepository flashcardRepo;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public List<Flashcard> list() {
        LOG.info("Getting all flashcards.");
        List<Flashcard> list = flashcardRepo.findAll();
        return list;
    }

    public List<Flashcard> listUserFlashcards(String userId) {
        LOG.info("Getting flashcards by userId in service.");
        List<Flashcard> list = flashcardRepo.findByUserId(userId);
        return list;
    }

    public Flashcard create(final Flashcard flashcard) {
        Flashcard fc = new Flashcard();
        boolean validFlashcard = isValid(flashcard);
        try {
            if (validFlashcard) {
                LOG.info("User input is valid. Inserting new flashcard into DB");
                fc = flashcardRepo.save(flashcard);
                LOG.info("New Flashcard ID: " + fc.getGameId());
            } else {
                throw new MongoException("Invalid entry into database");
            }
        } catch (MongoException exception) {
            LOG.error("Invalid entry into database");
        } catch (NullPointerException exception) {
            LOG.error("Insert into database returned null");
        }
        return fc;
    }

    public Flashcard getFlashcardById(String id) {
        return flashcardRepo.findByGameId(id);
    }

    public List<Flashcard> queryByQuestion(String question) {
        return flashcardRepo.findByQuestion(question);
    }

    public List<Flashcard> queryByAnswer(String answer) {
        return flashcardRepo.findByAnswer(answer);
    }

    @Override
    public boolean isValid(Flashcard obj) {
        List<Flashcard> result = list();
        return result.stream()
                .filter(fc -> fc.getQuestion().equalsIgnoreCase(obj.getQuestion()))
                .filter(fc -> fc.getAnswer().equalsIgnoreCase(obj.getAnswer())).toList().size() <= 0;
    }
}