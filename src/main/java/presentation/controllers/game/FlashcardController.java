package presentation.controllers.game;

import chemlab.domain.game.FlashcardService;
import chemlab.model.game.Flashcard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shared.FlashcardDto;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/flashcards")
@Slf4j
public class FlashcardController {

    @Autowired
    private FlashcardService flashcardService;

    @GetMapping(value = "/all")
    @ResponseBody
    public List<Flashcard> list() {
        log.trace("Getting all flashcards.");
        return flashcardService.list();
    }

    @GetMapping(value = "/userflashcards/{userId}")
    public ResponseEntity<List<Flashcard>> getFlashcardByUserId(@PathVariable("userId") String userId) {
        log.trace("Returning flashcards by userId: {} in controller.", userId);
        List<Flashcard> flashcards = flashcardService.listUserFlashcards(userId);
        return new ResponseEntity<>(flashcards, OK);
    }

    @GetMapping(value = "/questions")
    public List<Flashcard> queryQuestions(String question) {
        log.trace("Getting all flashcards that match the question");
        return flashcardService.queryByQuestion(question);
    }

    @GetMapping(value = "/answers")
    public List<Flashcard> queryAnswers(String answer) {
        log.trace("Getting all flashcards that match the answer");
        return flashcardService.queryByAnswer(answer);
    }

    @PostMapping("/add")
    public ResponseEntity<String> create(@RequestBody FlashcardDto flashcardDto) throws Exception {
        return flashcardService.create(flashcardDto) != null ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
