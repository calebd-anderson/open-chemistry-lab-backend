package chemlab.service.game;

import chemlab.domain.model.game.Flashcard;
import chemlab.domain.model.user.User;
import chemlab.domain.service.game.FlashcardService;
import chemlab.infrastructure.persistence.user.RegisteredUserRepository;
import chemlab.shared.requests.CreateFlashcardRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DefaultUserFlashcardService implements FlashcardService {

    @Autowired
    private RegisteredUserRepository userRepo;

    public List<Flashcard> listUserFlashcards(String userId) {
        log.trace("Getting flashcards by userId in service.");
        return userRepo.findByUserId(userId).getUserFlashcards();
    }

    public List<Flashcard> create(CreateFlashcardRequest flashcard) throws Exception {
        // map dto to actual
        ModelMapper modelMapper = new ModelMapper();
        Flashcard newFlashcard = modelMapper.map(flashcard, Flashcard.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        User user = userRepo.findByUsername(authentication.getName());

        if (!Objects.equals(flashcard.getUserId(), user.getUserId())) {
            throw new Exception();
        }

        log.trace("Adding flashcard to user.");
        List<Flashcard> userFlashcards = user.getUserFlashcards();
        userFlashcards.add(newFlashcard);
        user.setUserFlashcards(userFlashcards);
        return userRepo.save(user).getUserFlashcards();
    }
}