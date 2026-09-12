package chemlab.services;

import chemlab.domain.model.game.Flashcard;
import chemlab.shared.requests.CreateFlashcardRequest;
import chemlab.domain.model.user.User;
import chemlab.infrastructure.persistence.user.RegisteredUserRepository;
import chemlab.service.game.DefaultUserFlashcardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlashcardServiceTest {
    @Mock
    RegisteredUserRepository userRepo;
    @InjectMocks
    private DefaultUserFlashcardService flashcardService;

    @Test
    void findsFlashcards() {
        User user = mock(User.class);
        when(userRepo.findByUserId("123456"))
                .thenReturn(user);

        String answerYes = "yes";
        String answerNo = "no";
        when(userRepo.findByUserId("123456").getUserFlashcards()).thenReturn(List.of(
                new Flashcard("is this mock value 1?", answerYes),
                new Flashcard("is this mock value 2?", answerNo),
                new Flashcard("is this mock value 3?", answerYes),
                new Flashcard("is this mock value 4?", answerNo)
        ));
        List<Flashcard> result = flashcardService.listUserFlashcards("123456");
        assertEquals(4, result.size());
    }

    @Test
    void createFlashcardSuccess() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("testuser");
//        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // configure a user to hold the flashcard
        User user = new User();
        user.setUserId("12345");
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        // mock the repository to return the user when queried by username
        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(userRepo.save(user)).thenReturn(user);
        // create a flashcard
        CreateFlashcardRequest fc = new CreateFlashcardRequest("12345", "is this mock value 1?", "yes");
        // map the request to a flashcard
        ModelMapper modelMapper = new ModelMapper();
        Flashcard newFlashcard = modelMapper.map(fc, Flashcard.class);

        // Act
        List<Flashcard> result = flashcardService.create(fc);
        // Assert
        assertEquals(List.of(newFlashcard), result);
    }
}
