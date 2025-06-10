package chemlab.services;

import chemlab.services.chemistry.ElementService;
import com.mongodb.MongoException;
import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.Element;
import chemlab.model.game.Quiz;
import chemlab.repositories.game.QuizRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService implements ServiceInterface<Quiz> {
    @Autowired
    private QuizRepository quizRepo;

    @Autowired
    private ElementService elementService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public Quiz createQuiz(Quiz quiz) {
        Quiz q = null;

        try {
            if (isValid(quiz)) {
                LOG.info("Inserting new quiz into DB");
                q = quizRepo.save(quiz);
            } else {
                throw new MongoException("Invalid entry into database");
            }
        } catch (MongoException mongoException) {
            LOG.error("Invalid entry into database");
        }

        return q;
    }

    public void createQuizes(List<Quiz> quizList) {
        LOG.info("Create Quiz from list");
        quizList.forEach(quiz -> {
            if (isValid(quiz)) {
                LOG.info("Quiz is valid:" + quiz.toString());
                quizRepo.save(quiz);
            }
        });
    }

    public List<Quiz> list() {
        return quizRepo.findAll();
    }

    public Quiz getQuizById(String id) {
        if (quizRepo.findById(id).isPresent())
            return quizRepo.findById(id).get();
        else
            throw new MongoException("Quiz not found");

    }

    public void createNewQuizes(Reaction reaction, String userId, String quizType) {
        LOG.info("creating multiple compound quizes");
        List<Quiz> quizList = new ArrayList<>();

        if (quizType.equalsIgnoreCase("compound")) {
            String q1 = "What is the name of this compound: " + reaction.getFormula() + "?";
            String a1 = reaction.getTitle();
            String q2 = "What is the formula for " + reaction.getTitle() + "?";
            String a2 = reaction.getFormula();
            Quiz quiz1 = new Quiz(userId, q1, a1, quizType);
            Quiz quiz2 = new Quiz(userId, q2, a2, quizType);
            quizList.add(quiz1);
            quizList.add(quiz2);
        } else if (quizType.equalsIgnoreCase("element")) {
            reaction.getElements().keySet().stream().forEach((k) -> {
                LOG.info("creating a quiz for the element: " + k);
                Element element = null;
                element = elementService.getElementBySymbol(k);
                LOG.info("About to call element");
                LOG.info("Element: " + element.toString());
                String q1 = "What is the name of the element " + element.getSymbol() + "?";
                String a1 = element.getName();
                String q2 = "What is the symbol for " + element.getName() + "?";
                String a2 = element.getSymbol();

                Quiz quiz1 = new Quiz(userId, q1, a1, quizType);
                Quiz quiz2 = new Quiz(userId, q2, a2, quizType);

                quizList.add(quiz1);
                quizList.add(quiz2);
            });
        }

        createQuizes(quizList);
    }

    public List<Quiz> queryQuestions(String question) {

        return quizRepo.findByQuestion(question);
    }

    public List<Quiz> queryAnswers(String answer) {
        return quizRepo.findByAnswer(answer);
    }

    public List<Quiz> findQuizByUserId(String userId) {
        return quizRepo.findQuizByUserId(userId);
    }

    public List<Quiz> findQuizByQuizType(String quizType) {
        return quizRepo.findQuizByQuizType(quizType);
    }

    @Override
    public boolean isValid(Quiz obj) {
        String userId = obj.getUserId();
        List<Quiz> result = quizRepo.findAll();
        return ((userId != null && !userId.equalsIgnoreCase("")) && (result.stream()
                .filter(quiz -> quiz.getUserId().equalsIgnoreCase(obj.getUserId()))
                .filter(quiz -> quiz.getQuestion().equalsIgnoreCase(obj.getQuestion()))
                .filter(quiz -> quiz.getAnswer().equalsIgnoreCase(obj.getAnswer())).collect(Collectors.toList()).size() <= 0));
    }
}
