package chemlab.controller.api.game;

import chemlab.domain.game.FlashcardService;
import chemlab.model.game.Flashcard;
import chemlab.model.shared.CreateFlashcardRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/flashcards")
@Slf4j
public class FlashcardController {

    @Autowired
    private FlashcardService flashcardService;

    @GetMapping(value = "/userflashcards/{userId}")
    public ResponseEntity<List<Flashcard>> getFlashcardByUserId(@PathVariable("userId") String userId) {
        log.trace("Returning flashcards by userId: {} in controller.", userId);
        List<Flashcard> flashcards = flashcardService.listUserFlashcards(userId);
        return new ResponseEntity<>(flashcards, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<List<Flashcard>> create(@Valid @RequestBody CreateFlashcardRequest createFlashcardRequest) throws Exception {
        List<Flashcard> userFlashcards = flashcardService.create(createFlashcardRequest);
        if (userFlashcards != null && !userFlashcards.isEmpty()) {
            URI location = URI.create(String.format("/api/flashcards/userflashcards/%s", createFlashcardRequest.getUserId()));
            return ResponseEntity.created(location).body(userFlashcards);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
