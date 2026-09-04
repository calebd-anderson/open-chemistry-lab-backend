package chemlab.infrastructure.pubchem.service;

import chemlab.infrastructure.pubchem.PugApiResponse.FastformulaCidsResponse;
import chemlab.infrastructure.pubchem.PugApiResponse.FastformulaPropertiesResponse;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;

public interface PubChemApiService {
    FastformulaPropertiesResponse getFormulaProperties(String formula) throws PugApiException;
    FastformulaCidsResponse getFormulaCids(String formula) throws PugApiException;
}
