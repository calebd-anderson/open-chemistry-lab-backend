package chemlab.services;

import chemlab.exceptions.domain.PugApiException;
import chemlab.model.PugApiDTO;
import chemlab.model.chemistry.Reaction;
import chemlab.repositories.chemistry.ReactionRepository;
import chemlab.services.chemistry.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static chemlab.constants.PugApiConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReactionServiceTest {

    @Autowired
    private ReactionService reactionService;

    @MockitoBean
    private RestTemplate restMock;

    // mocked in setup()
    private ReactionRepository repoMock;

    @MockitoBean
    private QuizService quizMock;

    String compound = "NaCl";

    @BeforeEach
    public void setUp() {
        repoMock = mock(ReactionRepository.class);
        ReflectionTestUtils.setField(reactionService, "reactionRepo", repoMock);
        ReflectionTestUtils.setField(reactionService, "restTemplate", restMock);
        ReflectionTestUtils.setField(reactionService, "quizService", quizMock);
    }

    @Test
    @DisplayName("It should return false if the compound does not exist in the repo")
    void doesValueExistInRepo_false() {
        List<Reaction> mockValue = new ArrayList<>();
        Mockito.doReturn(mockValue).when(repoMock).findCompoundByFormula("H2O");
        assertFalse(reactionService.doesValueExistInRepo("H2O"));
    }

    @Test
    @DisplayName("It should return true if the compound exists in the repo")
    void doesValueExistInRepo_true() {
        String userId = "12345";
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);

        Reaction c1 = new Reaction(elements, userId);
        List<Reaction> mockValue = new ArrayList<>();
        mockValue.add(c1);
        Mockito.doReturn(mockValue).when(repoMock).findCompoundByFormula("H2O");

        assertTrue(reactionService.doesValueExistInRepo("H2O"));
    }

    @Test
    @DisplayName("It should return the value from the repo if it exists")
    void validateInput_returnFromRepo() throws PugApiException {
        String userId = "12345";
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);

        Reaction c1 = new Reaction(elements, userId);
        String formula = "H2O";
        List<Reaction> mockValue = new ArrayList<>();
        mockValue.add(c1);
        Mockito.doReturn(mockValue).when(repoMock).findCompoundByFormula(formula);

        assertNotNull(reactionService.validateInput(c1));

        verify(restMock, never()).getForObject(PUG_PROLOG + PUG_INPUT + formula + PUG_OPERATION + PUG_OUTPUT, HashMap.class);
    }

    @Test
    @DisplayName("It should return the value from PUG API if the value does not exist in the repo")
    void validateInput_returnFromPugApi() throws Exception {
        List<Reaction> mockValue = new ArrayList<>();
        String formula = "NaCl";
        String userId = "12345";
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("Na", 1);
        elements.put("Cl", 1);
        Mockito.doReturn(mockValue).when(repoMock).findCompoundByFormula("NaCl");
        Reaction c1 = new Reaction(elements, userId);
        Mockito.doReturn(c1).when(repoMock).save(c1);

        PugApiDTO pugApiMock = new PugApiDTO();
        pugApiMock.initializePropertyTableObj();
        pugApiMock.appendToPropertyTableObj(5234, "ClNa", "58.44", "Sodium chloride");
        Mockito.doReturn(pugApiMock).when(restMock).getForObject(PUG_PROLOG + PUG_INPUT + compound + PUG_OPERATION + PUG_OUTPUT, PugApiDTO.class);

        reactionService.validateInput(c1);

        verify(restMock, times(1)).getForObject(PUG_PROLOG + PUG_INPUT + formula + PUG_OPERATION + PUG_OUTPUT, PugApiDTO.class);
        verify(repoMock, times(1)).save(c1);
        verify(quizMock, times(1)).createNewQuizes(c1, c1.getUserId(), "compound");
        verify(quizMock, times(1)).createNewQuizes(c1, c1.getUserId(), "element");
    }
}
