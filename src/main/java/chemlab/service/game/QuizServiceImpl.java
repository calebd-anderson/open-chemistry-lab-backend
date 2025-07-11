package chemlab.service.game;

import chemlab.domain.game.QuizService;
import chemlab.model.chemistry.Reaction;
import chemlab.model.chemistry.UserReaction;
import chemlab.model.game.QuestionAnswer;
import chemlab.model.game.ReactionQuiz;
import chemlab.model.user.User;
import chemlab.repository.chemistry.ElementRepository;
import chemlab.repository.chemistry.ReactionRepository;
import chemlab.repository.game.quiz.QuizRepository;
import chemlab.repository.user.RegisteredUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shared.CreateQuizDto;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepo;
    @Autowired
    private ElementRepository elementRepo;
    @Autowired
    ReactionRepository reactionRepository;
    @Autowired
    private RegisteredUserRepository userRepo;

    public ReactionQuiz getQuizByFormula(String formula) {
        throw new NotImplementedException();
//        if (quizRepo.findByQuizType(QuizType.COMPOUND).isPresent())
//            return quizRepo.findByQuizType(QuizType.COMPOUND).get();
//        else
//            throw new MongoException("Quiz not found");
    }

    public ReactionQuiz createQuiz(CreateQuizDto quizDto) {
        String q1 = "What is the name of this compound: " + quizDto.getFormula() + "?";
        String a1 = quizDto.getReactionName();
        String q2 = "What is the formula for " + quizDto.getReactionName() + "?";
        String a2 = quizDto.getFormula();
        log.info("Looking in db for reaction with formula: {}", quizDto.getFormula());
        Reaction r1 = reactionRepository.findReactionByFormula(quizDto.getFormula());
        ReactionQuiz reactionQuiz = new ReactionQuiz(r1);
        reactionQuiz.setQuestionAnswerList(List.of(
                new QuestionAnswer(q1, a1),
                new QuestionAnswer(q2, a2)
        ));
        quizRepo.createFormulaQuiz(reactionQuiz);
        return reactionQuiz;
    }

    private void createQuizzes(List<ReactionQuiz> reactionQuizList) {
        log.trace("Create Quiz from list");
        reactionQuizList.forEach(quiz -> {
            quizRepo.saveFormulaQuiz(quiz);
        });
    }

    public List<ReactionQuiz> findQuizByUserId(String userId) {
        User user = userRepo.findRegisteredUserByUserId(userId);
        // go through user discovered reactions
        List<ReactionQuiz> quizzes = new ArrayList<>();
        for(UserReaction reaction : user.getDiscoveredReactions()) {
            // add formula/reaction quiz to bag
            ReactionQuiz quiz = quizRepo.findQuizByFormula(reaction.getUserDiscoveredReaction().getFormula());
            quizzes.add(quiz);
        }
        return quizzes;
    }
}
