package presentation.controllers.game;

import chemlab.domain.game.FlashcardService;
import chemlab.domain.model.game.Flashcard;
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
    private FlashcardService flashcardServiceImpl;

    @GetMapping(value = "/all")
    @ResponseBody
    public List<Flashcard> list() {
        log.trace("Getting all flashcards.");
        return flashcardServiceImpl.list();
    }

    @GetMapping("/{gameId}")
    @ResponseBody
    public Flashcard getFlashcardById(String id) {
        log.trace("Return flashcard by id.");
        return this.flashcardServiceImpl.getFlashcardById(id);
    }

    @GetMapping(value = "/userflashcards/{userId}")
    public ResponseEntity<List<Flashcard>> getFlashcardByUserId(@PathVariable("userId") String userId) {
        log.trace("Returning flashcards by userId: {} in controller.", userId);
        List<Flashcard> flashcards = flashcardServiceImpl.listUserFlashcards(userId);
        return new ResponseEntity<>(flashcards, OK);
    }

    @GetMapping(value = "/questions")
    public List<Flashcard> queryQuestions(String question) {
        log.trace("Getting all flashcards that match the question");
        return flashcardServiceImpl.queryByQuestion(question);
    }

    @GetMapping(value = "/answers")
    public List<Flashcard> queryAnswers(String answer) {
        log.trace("Getting all flashcards that match the answer");
        return flashcardServiceImpl.queryByAnswer(answer);
    }

    @PostMapping("/add")
    public ResponseEntity<String> create(@RequestBody final FlashcardDto flashcardDto) {
        Flashcard flashcard = new Flashcard(flashcardDto.getUserId(), flashcardDto.getQuestion(), flashcardDto.getAnswer());
        return flashcardServiceImpl.create(flashcard) != null ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
