package services.pubchem;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.exceptions.domain.PugApiException;

public interface PubChemApiService {
    Reaction testFormula(String formula, Reaction reaction) throws PugApiException;
}
