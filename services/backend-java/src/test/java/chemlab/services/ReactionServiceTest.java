package chemlab.services;

import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.model.chemistry.ReactionMapper;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.ReactionRepository;
import chemlab.domain.repository.UserRepository;
import chemlab.domain.service.user.UserReactionService;
import chemlab.infrastructure.fastapiworker.ClusterMapRequest;
import chemlab.infrastructure.fastapiworker.service.FastApiWorkerService;
import chemlab.infrastructure.pubchem.PugApiResponse.FastformulaPropertiesResponse;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import chemlab.infrastructure.pubchem.service.PubChemApiService;
import chemlab.service.chemistry.DefaultReactionService;
import chemlab.shared.requests.ReactionRequest;
import chemlab.shared.responses.ReactionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepo;
    @Mock
    private PubChemApiService pubChemApi;
    @Mock
    private ReactionMapper reactionMapper;
    @Mock
    private UserRepository userRepo;
    @Mock
    private UserReactionService userReactionService;
    @Mock
    private FastApiWorkerService fastApiWorkerService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private DefaultReactionService reactionService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    @BeforeEach
    void setUp() {
        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        if (mockedSecurityContextHolder != null) {
            mockedSecurityContextHolder.close();
        }
    }

    @Test
    @DisplayName("hasCompoundBeenDiscovered returns false when reaction not in repo")
    void hasCompoundBeenDiscovered_false() {
        when(reactionRepo.findReactionByFormula("H2O")).thenReturn(null);
        assertFalse(reactionService.hasCompoundBeenDiscovered("H2O"));
    }

    @Test
    @DisplayName("hasCompoundBeenDiscovered returns true when reaction exists in repo")
    void hasCompoundBeenDiscovered_true() {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        Reaction r1 = new Reaction(elements);
        when(reactionRepo.findReactionByFormula("H2O")).thenReturn(r1);

        assertTrue(reactionService.hasCompoundBeenDiscovered("H2O"));
    }

    @Test
    @DisplayName("createReaction returns existing reaction if already discovered")
    void createReaction_alreadyDiscovered() throws PugApiException {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        Reaction r1 = new Reaction(elements);
        String formula = "H2O";

        when(reactionRepo.findReactionByFormula(formula)).thenReturn(r1);
        when(reactionRepo.save(any(Reaction.class))).thenReturn(r1);

        ReactionRequest request = mock(ReactionRequest.class);
//        when(request.getMappedPayload()).thenReturn(elements);
        when(reactionMapper.toEntity(request)).thenReturn(r1);
        when(reactionMapper.toResponse(r1)).thenReturn(mock(ReactionResponse.class));

        reactionService.createReaction(request);

        verify(pubChemApi, never()).getFormulaProperties(anyString());
        verify(reactionRepo).save(r1);
    }

    @Test
    @DisplayName("createReaction fetches from PubChem if not discovered")
    void createReaction_notDiscovered() throws PugApiException {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("Na", 1);
        elements.put("Cl", 1);
        Reaction r1 = new Reaction(elements);
        String formula = "NaCl";

        when(reactionRepo.findReactionByFormula(formula)).thenReturn(null);
        FastformulaPropertiesResponse response = mock(FastformulaPropertiesResponse.class);
        when(pubChemApi.getFormulaProperties(formula)).thenReturn(response);
        when(reactionRepo.save(any(Reaction.class))).thenReturn(r1);

        ReactionRequest request = mock(ReactionRequest.class);
//        when(request.getMappedPayload()).thenReturn(elements);
        when(reactionMapper.toEntity(request)).thenReturn(r1);
        when(reactionMapper.toResponse(r1)).thenReturn(mock(ReactionResponse.class));

        reactionService.createReaction(request);

        verify(pubChemApi).getFormulaProperties(formula);
        verify(reactionRepo).save(r1);
    }

    @Test
    @DisplayName("createReaction handles authenticated user by saving to user reaction service")
    void createReaction_authenticatedUser() throws PugApiException {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        Reaction r1 = new Reaction(elements);
        String formula = "H2O";

        when(reactionRepo.findReactionByFormula(formula)).thenReturn(r1);
        when(reactionRepo.save(any(Reaction.class))).thenReturn(r1);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");

        User user = mock(User.class);
        when(user.getUserId()).thenReturn("user-100");
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        ReactionRequest request = mock(ReactionRequest.class);
//        when(request.getMappedPayload()).thenReturn(elements);
        when(reactionMapper.toEntity(request)).thenReturn(r1);
        when(reactionMapper.toResponse(r1)).thenReturn(mock(ReactionResponse.class));

        reactionService.createReaction(request);

        verify(userReactionService).saveReactionWithUser(eq("user-100"), any(Reaction.class));
    }

    @Test
    @DisplayName("analyzeFormula calls PubChem and FastApiWorker")
    void analyzeFormula_success() throws Exception {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        ReactionRequest request = mock(ReactionRequest.class);
        when(request.getMappedPayload()).thenReturn(elements);

        FastformulaPropertiesResponse response = mock(FastformulaPropertiesResponse.class);
        when(pubChemApi.getFormulaProperties("H2O")).thenReturn(response);

        List<ClusterMapRequest> clusters = List.of(mock(ClusterMapRequest.class));
        when(fastApiWorkerService.analyzePubChemFastformulaProps(response)).thenReturn(clusters);

        List<ClusterMapRequest> result = reactionService.analyzeFormula(request);

        assertNotNull(result);
        assertEquals(clusters, result);
        verify(pubChemApi).getFormulaProperties("H2O");
        verify(fastApiWorkerService).analyzePubChemFastformulaProps(response);
    }
}
