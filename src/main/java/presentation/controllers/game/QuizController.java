package presentation.controllers.game;

import chemlab.domain.game.QuizService;
import chemlab.domain.model.game.Quiz;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shared.CreateQuizDto;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private QuizService quizService;

    @GetMapping("/all")
    @ResponseBody
    public List<Quiz> all() {
        return quizService.list();
    }

    @GetMapping(value = "{gameId}")
    public Quiz getQuizById(ObjectId id) {
        return quizService.getQuizById(id);
    }

    @GetMapping(value = "/questions")
    public List<Quiz> queryQuestions(String question) {
        LOG.info("Getting all quizzes that match the question");
        return quizService.queryQuestions(question);
    }

    @GetMapping(value = "/answers")
    public List<Quiz> queryAnswers(String answer) {
        LOG.info("Getting all quizzes");
        return quizService.queryAnswers(answer);
    }

//    @PostMapping("/add")
//    public Quiz create(@RequestBody CreateQuizDto quiz) {
//        return quizService.createQuiz(quiz);
//    }

    @GetMapping("/getbyuserid/{userId}")
    public List<Quiz> getByUserId(@PathVariable("userId") String userId) {
        return this.quizService.findQuizByUserId(userId);
    }

    @GetMapping("/getByQuizType")
    public List<Quiz> getByQuizType(@RequestParam String quizType) {
        return this.quizService.findQuizByQuizType(quizType);
    }
}
