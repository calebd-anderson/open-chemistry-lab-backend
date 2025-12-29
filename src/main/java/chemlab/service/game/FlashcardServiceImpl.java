package chemlab.service.game;

import chemlab.domain.ServiceInterface;
import chemlab.domain.game.FlashcardService;
import chemlab.model.game.Flashcard;
import chemlab.model.user.User;
import chemlab.repository.game.flashcard.FlashcardRepository;
import chemlab.repository.user.RegisteredUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import chemlab.model.shared.FlashcardDto;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FlashcardServiceImpl implements ServiceInterface<Flashcard>, FlashcardService {

    @Autowired
    private FlashcardRepository flashcardRepo;
    @Autowired
    private RegisteredUserRepository userRepo;

    public List<Flashcard> list() {
        log.info("Getting all flashcards.");
        List<Flashcard> list = flashcardRepo.findAll();
        return list;
    }

    public List<Flashcard> listUserFlashcards(String userId) {
        log.info("Getting flashcards by userId in service.");
        User user = userRepo.findRegisteredUserByUserId(userId);
        List<Flashcard> list = user.getUserFlashcards();
        return list;
    }

    public List<Flashcard> create(FlashcardDto flashcard) throws Exception {
        // map dto to actual
        ModelMapper modelMapper = new ModelMapper();
        Flashcard newFlashcard = modelMapper.map(flashcard, Flashcard.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findRegisteredUserByUsername(authentication.getName());

        if (!Objects.equals(flashcard.getUserId(), user.getUserId())) {
            throw new Exception();
        }
        // I don't know what this is for
        boolean validFlashcard = isValid(newFlashcard);

        log.trace("add flashcard to user");
        List<Flashcard> userFlashcards = user.getUserFlashcards();
        userFlashcards.add(newFlashcard);
        user.setUserFlashcards(userFlashcards);
        userRepo.save(user);
        return user.getUserFlashcards();
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