package chemlab.controller.api.game;

import chemlab.domain.model.game.UserQuiz;
import chemlab.domain.service.game.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/getbyuserid/{userId}")
    public List<UserQuiz> getByUserId(@PathVariable("userId") String userId) {
        LOG.info("getByUserId {}", userId);
        return this.quizService.findQuizByUserId(userId);
    }
}
