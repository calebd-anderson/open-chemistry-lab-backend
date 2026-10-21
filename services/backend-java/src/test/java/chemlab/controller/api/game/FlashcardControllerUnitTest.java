package chemlab.controller.api.game;

import chemlab.domain.model.game.Flashcard;
import chemlab.domain.service.game.FlashcardService;
import chemlab.security.jwt.JwtUserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlashcardControllerUnitTest {
    @Mock
    FlashcardService flashcardService;

    @Test
    void returnsFlashcardsForAuthenticatedUser() {
        var principal = new JwtUserPrincipal("test-user", "12345");
        var flashcards = List.of(new Flashcard("Question?", "Answer"));
        when(flashcardService.listUserFlashcards("12345")).thenReturn(flashcards);

        var controller = new FlashcardController(flashcardService);
        ResponseEntity<List<Flashcard>> response = controller.getFlashcardByUserId(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(flashcards, response.getBody());
        verify(flashcardService).listUserFlashcards("12345");
    }
}