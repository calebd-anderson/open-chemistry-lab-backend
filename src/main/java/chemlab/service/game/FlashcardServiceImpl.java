package chemlab.service.game;

import chemlab.domain.game.FlashcardService;
import chemlab.model.game.Flashcard;
import chemlab.model.shared.CreateFlashcardRequest;
import chemlab.model.user.User;
import chemlab.repository.user.RegisteredUserRepository;
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
public class FlashcardServiceImpl implements FlashcardService {

    @Autowired
    private RegisteredUserRepository userRepo;

    public List<Flashcard> listUserFlashcards(String userId) {
        log.info("Getting flashcards by userId in service.");
        return userRepo.findRegisteredUserByUserId(userId).getUserFlashcards();
    }

    public List<Flashcard> create(CreateFlashcardRequest flashcard) throws Exception {
        // map dto to actual
        ModelMapper modelMapper = new ModelMapper();
        Flashcard newFlashcard = modelMapper.map(flashcard, Flashcard.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        User user = userRepo.findRegisteredUserByUsername(authentication.getName());

        if (!Objects.equals(flashcard.getUserId(), user.getUserId())) {
            throw new Exception();
        }

        log.trace("add flashcard to user");
        List<Flashcard> userFlashcards = user.getUserFlashcards();
        userFlashcards.add(newFlashcard);
        user.setUserFlashcards(userFlashcards);
        return userRepo.save(user).getUserFlashcards();
    }
}