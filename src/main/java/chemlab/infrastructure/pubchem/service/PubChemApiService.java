package chemlab.infrastructure.pubchem.service;

import chemlab.infrastructure.pubchem.PugApiResponse.FastformulaPropertiesResponse;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;

public interface PubChemApiService {
    FastformulaPropertiesResponse testFormula(String formula) throws PugApiException;
}
