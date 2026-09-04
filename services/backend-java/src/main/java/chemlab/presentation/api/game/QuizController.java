package chemlab.presentation.api.game;

import chemlab.domain.service.game.QuizService;
import chemlab.domain.model.game.UserQuiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private QuizService quizService;

    @GetMapping("/getbyuserid/{userId}")
    public List<UserQuiz> getByUserId(@PathVariable("userId") String userId) {
        return this.quizService.findQuizByUserId(userId);
    }
}
