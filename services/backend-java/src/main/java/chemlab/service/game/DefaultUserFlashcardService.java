package chemlab.service.game;

import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.model.game.Flashcard;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.UserRepository;
import chemlab.domain.service.game.FlashcardService;
import chemlab.shared.requests.CreateFlashcardRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DefaultUserFlashcardService implements FlashcardService {

    @Autowired
    private UserRepository userRepo;

    public List<Flashcard> listUserFlashcards(String userId) {
        log.trace("Getting flashcards by userId in service.");
        User user = userRepo.findByUserId(userId).orElseThrow();
        return user.getUserFlashcards();
    }

    public List<Flashcard> create(CreateFlashcardRequest flashcard) throws Exception {
        // map dto to actual
        ModelMapper modelMapper = new ModelMapper();
        Flashcard newFlashcard = modelMapper.map(flashcard, Flashcard.class);

        User user = userRepo.findByUserId(flashcard.getUserId()).orElseThrow();

        log.trace("Adding flashcard to user.");
        List<Flashcard> userFlashcards = user.getUserFlashcards();
        userFlashcards.add(newFlashcard);
        return userRepo.save(user).getUserFlashcards();
    }

    public void delete(String username, String question) throws UserNotFoundException {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        user.getUserFlashcards().removeIf(flashcard -> flashcard.getQuestion().equals(question));
        userRepo.save(user);
    }
}