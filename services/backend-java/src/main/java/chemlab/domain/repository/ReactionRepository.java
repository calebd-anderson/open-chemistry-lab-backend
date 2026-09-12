package chemlab.domain.repository;

import chemlab.domain.model.chemistry.Reaction;

import java.util.List;

public interface ReactionRepository {
    Reaction findReactionByFormula(String formula);
    List<Reaction> findAll();
    Reaction save(Reaction reaction);
    void deleteAll();
}
