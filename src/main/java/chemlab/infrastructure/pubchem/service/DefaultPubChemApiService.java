package chemlab.infrastructure.pubchem.service;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.infrastructure.pubchem.PugApiResponse.FastformulaPropertiesResponse;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static chemlab.infrastructure.pubchem.PugApiConstants.*;

@Slf4j
@Service
public class DefaultPubChemApiService implements PubChemApiService {

    @Autowired
    private RestTemplate restTemplate;

    public Reaction testFormula(String formula, Reaction reaction) throws PugApiException {
        log.info("Calling PubChem API with molecular formula: {}", formula);
        String pubChemUrl = PUG_PROLOG + PUG_INPUT + formula + PUG_OPERATION + PUG_OUTPUT;
        try {
            log.trace("Sending PugAPI url in service: {}", pubChemUrl);
            FastformulaPropertiesResponse pugApiValue = restTemplate.getForObject(pubChemUrl, FastformulaPropertiesResponse.class);
            assert pugApiValue != null;
            reaction.setTitle(pugApiValue.getFirstPropertyTitle());
            log.info("Found reaction: {}", pugApiValue.getFirstPropertyTitle());
            return reaction;
        } catch (HttpStatusCodeException exception) {
            log.warn("Received {} error response from PUG API: {}", exception.getStatusCode(), exception.getResponseBodyAsString());
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new PugApiException("404 error from PubChem API url: " + pubChemUrl);
            } else
                throw new PugApiException("Unexpected PubChem API error");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new RuntimeException("Unexpected PubChem API error");
        }
    }
}
