package chemlab.infrastructure.pubchem.service;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;

public interface PubChemApiService {
    Reaction testFormula(String formula, Reaction reaction) throws PugApiException;
}
