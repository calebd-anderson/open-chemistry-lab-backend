package infrastructure.pubchem;

import chemlab.model.chemistry.Reaction;
import infrastructure.pubchem.exceptions.PugApiException;

public interface PubChemApiService {
    Reaction testFormula(String formula, Reaction reaction) throws PugApiException;
}
