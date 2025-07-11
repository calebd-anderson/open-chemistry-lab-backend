package chemlab.services;

import chemlab.domain.chemistry.ReactionService;
import chemlab.domain.game.FlashcardService;
import chemlab.domain.user.UserReactionService;
import chemlab.model.chemistry.Reaction;
import chemlab.repository.chemistry.ReactionRepository;
import chemlab.repository.game.flashcard.FlashcardRepository;
import chemlab.service.chemistry.ReactionServiceImpl;
import infrastructure.pubchem.PubChemApiService;
import infrastructure.pubchem.exceptions.PugApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReactionServiceTest {


    @Mock
    private ReactionRepository reactionRepo;
    @Mock
    private UserReactionService userReactionService;
    @Mock
    FlashcardService flashcardService;
    @Mock
    FlashcardRepository flashcardRepository;

    @Mock
    private PubChemApiService pubChemApi;

    @InjectMocks
    private ReactionService reactionService = new ReactionServiceImpl();

    @Test
    @DisplayName("compound not yet discovered")
    void hasCompoundBeenDiscovered_false() {
        doReturn(null).when(reactionRepo).findReactionByFormula("H2O");
        assertFalse(reactionService.hasCompoundBeenDiscovered("H2O"));
    }

    @Test
    @DisplayName("compound has been discovered before")
    void hasCompoundBeenDiscovered_true() {
        // Arrange
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        // create reaction from elements
        Reaction r1 = new Reaction(elements);
        // stub in the reaction repository behavior
        doReturn(r1).when(reactionRepo).findReactionByFormula("H2O");

        // Act
        boolean result = reactionService.hasCompoundBeenDiscovered("H2O");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("It should return the value from the repo if it exists")
    void validateInput_returnFromRepo() throws PugApiException {
        // Arrange
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        // create reaction from elements
        Reaction r1 = new Reaction(elements);

        String formula = "H2O";
        // stub in the repo finds the reaction
        doReturn(r1).when(reactionRepo).findReactionByFormula(formula);
        // the service returns the saved reaction
        doReturn(r1).when(reactionRepo).save(r1);

        // Act
        Reaction reactionResult = reactionService.validateInput(r1);

        // Assert
        assertNotNull(reactionResult);
        // PubChem api not called when reaction already discovered
        verify(pubChemApi, never()).testFormula(formula, r1);
    }

    @Test
    @DisplayName("PubChem api is called when reaction not yet discovered")
    void validateInput_returnFromPugApi() throws Exception {
        // Arrange
        // setup a compound
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("Na", 1);
        elements.put("Cl", 1);
        // create a reaction from the elements
        Reaction r1 = new Reaction(elements);

        // TODO: ...
//        PugApiDto pugApiMock = new PugApiDto();
//        pugApiMock.initializePropertyTableObj();
//        pugApiMock.appendToPropertyTableObj(
//                5234,
//                "ClNa",
//                "58.44",
//                "Sodium chloride"
//        );

        String formula = "NaCl";
        // stub in the repo will not find the reaction
        doReturn(null).when(reactionRepo).findReactionByFormula(formula);
        // stub in the api return
        doReturn(r1).when(pubChemApi).testFormula(formula, r1);
        // stub in the service returns the saved reaction
        doReturn(r1).when(reactionRepo).save(r1);

        // Act
        Reaction reactionResult = reactionService.validateInput(r1);

        // Assert
        assertNotNull(reactionResult);
        // PubChem api called when reaction not yet discovered
        verify(pubChemApi, atLeastOnce()).testFormula(formula, r1);
        verify(reactionRepo, atLeastOnce()).save(r1);

        // TODO: setup the game stuff again
        // verify(quizMock, times(1)).createNewQuizes(c1, userId, "compound");
        // verify(quizMock, times(1)).createNewQuizes(c1, userId, "element");
    }

    @Test
    @DisplayName("Discovery count increments.")
    void discoveryCountIncrements() throws PugApiException {
        // Arrange
        // make discovery
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        // create reaction from elements
        Reaction r1 = new Reaction(elements);
        int initialDiscoveryCount = r1.getDiscoveredCount();
        // create list of reactions
//        List<Reaction> reactions = new ArrayList<>();
//        reactions.add(r1);
        String formula = "H2O";
        // stub in the repo finds the reaction
//        doReturn(reactions).when(reactionRepo).findCompoundByFormula(formula);
        // stub in the api return
        doReturn(r1).when(pubChemApi).testFormula(formula, r1);
        // stub in the service returns the saved reaction
        doReturn(r1).when(reactionRepo).save(r1);

        // check if discovery is recorded
        // if not record in mongodb

        // set discovery attributes:
        // discoveredWhen
        // discoveredBy
        // timesDiscovered
        // lastDiscoveredWhen
        // lasterDiscoveredBy
        // if so retrieve from database
        // set discovery attributes:
        // timesDiscovered
        // lastDiscoveredWhen
        // lasterDiscoveredBy

        // Act
        Reaction reactionResult = reactionService.validateInput(r1);

        // Assert
        // reaction discovered for first time so PubChem api called
        verify(pubChemApi, atLeastOnce()).testFormula(formula, r1);
        // after discovery (validateInput) the reaction discovery count is incremented by 1
        assertEquals(initialDiscoveryCount + 1, reactionResult.getDiscoveredCount());
    }
}
