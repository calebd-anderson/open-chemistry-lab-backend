package presentation.controllers.api.game;

import chemlab.domain.game.QuizService;
import chemlab.model.game.ReactionQuiz;
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

    @GetMapping("/getbyuserid/{userId}")
    public List<ReactionQuiz> getByUserId(@PathVariable("userId") String userId) {
        return this.quizService.findQuizByUserId(userId);
    }
}
