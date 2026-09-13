package chemlab.infrastructure.persistence.chemistry;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReactionPersistenceAdapter implements ReactionRepository {

    @Autowired
    SpringDataReactionRepository mongoRepository;

    @Override
    public Reaction findReactionByFormula(String formula) {
        return mongoRepository.findReactionByFormula(formula);
    }

    @Override
    public List<Reaction> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public Reaction save(Reaction reaction) {
        return mongoRepository.save(reaction);
    }

    @Override
    public void deleteAll() {
        mongoRepository.deleteAll();
    }
}
