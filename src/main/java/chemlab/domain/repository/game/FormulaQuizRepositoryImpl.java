package chemlab.domain.repository.game;

import chemlab.domain.model.game.Quiz;
import chemlab.domain.model.game.ReactionQuiz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static chemlab.domain.model.game.QuizType.COMPOUND;
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
        Quiz quiz = mongoTemplate.findOne(query(where("quizType").is(COMPOUND)), Quiz.class);
        try {
            if (quiz == null) {
                log.trace("Creating initial quiz document.");
                mongoTemplate.insert(new Quiz(COMPOUND));
            }
        } catch (DuplicateKeyException ex) {
            log.warn("Quiz document already exists with type: {}", COMPOUND);
        }

        List<ReactionQuiz> reactionQuizzes = mongoTemplate
                .findOne(query(where("quizType")
                        .is(COMPOUND)), Quiz.class)
                .getReactionQuizzes();

        for (ReactionQuiz rquiz : reactionQuizzes) {
            if (rquiz.getReaction().getFormula().equals(reactionQuiz.getReaction().getFormula())) {
                log.warn("Formula quiz already exists with formula: {}", reactionQuiz.getReaction().getFormula());
                return;
            }
        }

        Query query = Query.query(Criteria.where("quizType").is(COMPOUND));
        Update update = new Update().push("reactionQuizzes", reactionQuiz); // Push a single element
        mongoTemplate.updateFirst(query, update, Quiz.class);
    }
}
