package chemlab.services.chemistry;

import chemlab.exceptions.domain.PugApiException;
import chemlab.model.PugApiDTO;
import chemlab.model.chemistry.Compound;
import chemlab.repositories.chemistry.ReactionRepository;
import chemlab.services.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static chemlab.constants.PugApiConstants.*;

@Service
public class ReactionService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ReactionRepository reactionRepo;

    @Autowired
    private QuizService quizService;


    /**
     * TODO:
     * - Add handler for when request to PUG API doesn't return element
     * - Add local variable for last request sent (6 second delay if within window of last request)
     * -
     */

    public boolean doesValueExistInRepo(String formula) {
        LOG.info("Checking if [{}] exists in db.", formula);
        if (!reactionRepo.findCompoundByFormula(formula).isEmpty()) {
            LOG.info("[{}] found in db.", formula);
            return true;
        } else {
            LOG.info("[{}] not found in db.", formula);
            return false;
        }
    }

    private Compound retrieveCompoundFromRepo(String formula) {
        LOG.info("Value [{}] exists in repo, retrieving...", formula);
        return reactionRepo.findCompoundByFormula(formula).get(0);
    }

    private Compound retrieveCompoundFromPugApi(String formula, Compound compound) throws PugApiException {
        LOG.info("Calling PUG API with argument: {}", formula);
        String pubChemUrl = PUG_PROLOG + PUG_INPUT + formula + PUG_OPERATION + PUG_OUTPUT;
        try {
//            LOG.info("Sending PugAPI url in service: {}", pubChemUrl);
            PugApiDTO pugApiValue = restTemplate.getForObject(pubChemUrl, PugApiDTO.class);
            assert pugApiValue != null;
            compound.setTitle(pugApiValue.getFirstPropertyTitle());
            if (compound.getUserId() != null) {
                quizService.createNewQuizes(compound, compound.getUserId(), "compound");
                quizService.createNewQuizes(compound, compound.getUserId(), "element");
                return reactionRepo.save(compound);
            } else
                return compound;
        } catch (HttpStatusCodeException exception) {
            // to do: parse the JSON returned as an error to get a useful response
            // LOG.error(exception.getResponseBodyAsString());
//            LOG.error("Received " + exception.getStatusCode().value() + " response code from PUG API.");
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new PugApiException("404 error from PugAPI url: " + pubChemUrl);
            } else
                throw new PugApiException("pug api error");
        }
    }

    public List<Compound> getCompoundsByUserId(String userId) {
        return reactionRepo.findCompoundByUserId(userId);
    }

    public Compound validateInput(Compound compound) throws PugApiException {
        String formula = compound.getFormula();
        LOG.info("Validating: [{}]", formula);
        return doesValueExistInRepo(formula) ? retrieveCompoundFromRepo(formula) : retrieveCompoundFromPugApi(formula, compound);
    }
}
