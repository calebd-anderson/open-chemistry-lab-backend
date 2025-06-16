package presentation.controllers.game;

import chemlab.domain.game.QuizService;
import chemlab.domain.model.game.FormulaQuiz;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private QuizService quizService;

    @GetMapping("/all")
    @ResponseBody
    public List<FormulaQuiz> all() {
        return quizService.list();
    }

    @GetMapping(value = "{gameId}")
    public FormulaQuiz getQuizById(ObjectId id) {
        return quizService.getQuizById(id);
    }


//    @PostMapping("/add")
//    public Quiz create(@RequestBody CreateQuizDto quiz) {
//        return quizService.createQuiz(quiz);
//    }

    @GetMapping("/getbyuserid/{userId}")
    public List<FormulaQuiz> getByUserId(@PathVariable("userId") String userId) {
        return this.quizService.findQuizByUserId(userId);
    }
}
