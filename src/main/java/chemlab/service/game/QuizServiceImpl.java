package chemlab.service.game;

import chemlab.domain.game.QuizService;
import chemlab.domain.model.game.FormulaQuiz;
import chemlab.domain.model.game.QuestionAnswer;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.chemistry.ElementRepository;
import chemlab.domain.repository.game.QuizRepository;
import chemlab.domain.repository.user.RegisteredUserRepository;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shared.CreateQuizDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepo;
    @Autowired
    private ElementRepository elementRepo;
    @Autowired
    private RegisteredUserRepository userRepo;

    public List<FormulaQuiz> list() {
        return quizRepo.findAll();
    }

    public FormulaQuiz getQuizById(ObjectId id) {
        if (quizRepo.findById(id).isPresent())
            return quizRepo.findById(id).get();
        else
            throw new MongoException("Quiz not found");
    }

    public FormulaQuiz getQuizByFormula(String formula) {
        if (quizRepo.findByFormula(formula).isPresent())
            return quizRepo.findByFormula(formula).get();
        else
            throw new MongoException("Quiz not found");
    }

    public FormulaQuiz createQuiz(CreateQuizDto quizDto) {
        String q1 = "What is the name of this compound: " + quizDto.getFormula() + "?";
        String a1 = quizDto.getReactionName();
        String q2 = "What is the formula for " + quizDto.getReactionName() + "?";
        String a2 = quizDto.getFormula();

        FormulaQuiz formulaQuiz = new FormulaQuiz(quizDto.getFormula());
        formulaQuiz.setQuestionAnswerList(List.of(
                new QuestionAnswer(q1, a1),
                new QuestionAnswer(q2, a2)
        ));
        formulaQuiz = quizRepo.save(formulaQuiz);
        return formulaQuiz;
    }

    private void createQuizzes(List<FormulaQuiz> formulaQuizList) {
        log.trace("Create Quiz from list");
        formulaQuizList.forEach(quiz -> {
            quizRepo.save(quiz);
        });
    }

    public List<FormulaQuiz> findQuizByUserId(String userId) {
        User user = userRepo.findRegisteredUserByUserId(userId);
        return user.getQuizzes();
    }
}
