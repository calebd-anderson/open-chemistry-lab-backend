package chemlab.service.game;

import chemlab.domain.ServiceInterface;
import chemlab.domain.game.QuizService;
import chemlab.domain.model.chemistry.Element;
import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.game.Quiz;
import chemlab.domain.model.game.QuizType;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.chemistry.ElementRepository;
import chemlab.domain.repository.game.QuizRepository;
import chemlab.domain.repository.user.RegisteredUserRepository;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shared.CreateQuizDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuizServiceImpl implements ServiceInterface<Quiz>, QuizService {

    @Autowired
    private QuizRepository quizRepo;
    @Autowired
    private ElementRepository elementRepo;
    @Autowired
    private RegisteredUserRepository userRepo;

    public List<Quiz> list() {
        return quizRepo.findAll();
    }

    public Quiz getQuizById(ObjectId id) {
        if (quizRepo.findById(id).isPresent())
            return quizRepo.findById(id).get();
        else
            throw new MongoException("Quiz not found");
    }

    public List<ObjectId> createQuiz(CreateQuizDto quizDto) {
        String q1 = "What is the name of this compound: " + quizDto.getFormula() + "?";
        String a1 = quizDto.getReactionName();
        String q2 = "What is the formula for " + quizDto.getReactionName() + "?";
        String a2 = quizDto.getFormula();

        Quiz quiz1 = new Quiz(quizDto.quizType, quizDto.getFormula(), q1, a1);
        Quiz quiz2 = new Quiz(quizDto.quizType, quizDto.getFormula(), q2, a2);

        quiz1 = quizRepo.save(quiz1);
        quiz2 = quizRepo.save(quiz2);
        List<ObjectId> quizIds = List.of(quiz1.getId(), quiz2.getId());
        return quizIds;
    }

    public void createNewQuizzes(Reaction reaction, QuizType quizType) {
        log.info("creating multiple compound quizzes");
        List<Quiz> quizList = new ArrayList<>();

        if (quizType.equals(QuizType.COMPOUND)) {
            String q1 = "What is the name of this compound: " + reaction.getFormula() + "?";
            String a1 = reaction.getTitle();
            String q2 = "What is the formula for " + reaction.getTitle() + "?";
            String a2 = reaction.getFormula();

            Quiz quiz1 = new Quiz(quizType, reaction.getFormula(), q1, a1);
            Quiz quiz2 = new Quiz(quizType, reaction.getFormula(), q2, a2);

            quizList.add(quiz1);
            quizList.add(quiz2);
        } else if (quizType.equals(QuizType.ELEMENT)) {
            reaction.getElements().keySet().forEach((k) -> {
                log.info("creating a quiz for the element: " + k);
                Element element = null;
                element = elementRepo.findElementBySymbol(k);
                log.info("About to call element");
                log.info("Element: " + element.toString());
                String q1 = "What is the name of the element " + element.getSymbol() + "?";
                String a1 = element.getName();
                String q2 = "What is the symbol for " + element.getName() + "?";
                String a2 = element.getSymbol();

                Quiz quiz1 = new Quiz(quizType, element.getSymbol(), q1, a1);
                Quiz quiz2 = new Quiz(quizType, element.getSymbol(), q2, a2);

                quizList.add(quiz1);
                quizList.add(quiz2);
            });
        }
        createQuizzes(quizList);
    }

    private void createQuizzes(List<Quiz> quizList) {
        log.trace("Create Quiz from list");
        quizList.forEach(quiz -> {
            quizRepo.save(quiz);
        });
    }

    public List<Quiz> queryQuestions(String question) {
        return quizRepo.findByQuestion(question);
    }

    public List<Quiz> queryAnswers(String answer) {
        return quizRepo.findByAnswer(answer);
    }

    public List<Quiz> findQuizByUserId(String userId) {
        User user = userRepo.findRegisteredUserByUserId(userId);
        List<ObjectId> quizIds = user.getQuizzes();

        List<Quiz> quizzes = new ArrayList<>();
        for (ObjectId id : quizIds) {
            Optional<Quiz> quiz = quizRepo.findById(id);
            quiz.ifPresent(quizzes::add);
        }
        return quizzes;
    }

    public List<Quiz> findQuizByQuizType(String quizType) {
        return quizRepo.findQuizByQuizType(quizType);
    }

    @Override
    public boolean isValid(Quiz obj) {
        List<Quiz> result = quizRepo.findAll();
        return (result.stream()
                .filter(quiz -> quiz.getQuestion().equalsIgnoreCase(obj.getQuestion()))
                .filter(quiz -> quiz.getAnswer().equalsIgnoreCase(obj.getAnswer())).toList().size() <= 0);
    }
}
