package chemlab.repository.game;

import chemlab.model.game.Quiz;
import chemlab.model.game.ReactionQuiz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

import static chemlab.model.game.QuizType.COMPOUND;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
public class FormulaQuizRepositoryImpl implements FormulaQuizRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ReactionQuiz findQuizByFormula(String formula) {
        List<ReactionQuiz> reactionQuizzes = mongoTemplate
            .findOne(query(where("quizType")
                    .is(COMPOUND)), Quiz.class)
            .getReactionQuizzes();

        for (ReactionQuiz rquiz : reactionQuizzes) {
            if (rquiz.getReaction().getFormula().equals(formula)) {
                return rquiz;
            }
        }
        return null;
    }

    @Override
    public void saveFormulaQuiz(ReactionQuiz quiz) {
        throw new NotImplementedException();
    }

    @Override
    public void createFormulaQuiz(ReactionQuiz reactionQuiz) {
        try {
            log.info("Looking for quiz by type: {}", COMPOUND);
            if (mongoTemplate.findOne(query(where("quizType").is(COMPOUND)), Quiz.class) == null) {
                log.info("Creating initial quiz document.");
                Quiz newQuiz = new Quiz(COMPOUND);
                newQuiz.setReactionQuizzes(new ArrayList<>());
                mongoTemplate.insert(newQuiz);
            }
        } catch (DuplicateKeyException ex) {
            log.warn("Quiz document already exists with type: {}", COMPOUND);
        }

//        Quiz quiz = mongoTemplate.findOne(query(where("quizType").is(COMPOUND)), Quiz.class);
        log.info("Adding reactionQuizzes to bag.");
        List<ReactionQuiz> reactionQuizzes = mongoTemplate
                .findOne(query(where("quizType")
                        .is(COMPOUND)), Quiz.class)
                .getReactionQuizzes();

        if (reactionQuizzes != null && !reactionQuizzes.isEmpty()) {
            log.info("Checking if reaction already exists on non-empty reactionQuizzes.");
            for (ReactionQuiz rquiz : reactionQuizzes) {
                if (rquiz.getReaction().getFormula().equals(reactionQuiz.getReaction().getFormula())) {
                    log.warn("Formula quiz already exists with formula: {}", reactionQuiz.getReaction().getFormula());
                    return;
                }
            }
        }

        log.info("Pushing new reaction quiz with formula [{}] onto reactionQuizzes", reactionQuiz.getReaction().getFormula());
        Query query = Query.query(Criteria.where("quizType").is(COMPOUND));
        Update update = new Update().push("reactionQuizzes", reactionQuiz); // Push a single element
        mongoTemplate.updateFirst(query, update, Quiz.class);
    }
}
