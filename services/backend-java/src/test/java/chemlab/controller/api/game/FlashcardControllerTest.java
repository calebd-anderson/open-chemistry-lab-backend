package chemlab.controller.api.game;

import chemlab.domain.model.game.Flashcard;
import chemlab.domain.service.game.FlashcardService;
import chemlab.infrastructure.error.ProblemDetailFactory;
import chemlab.security.jwt.JwtTokenProvider;
import chemlab.security.jwt.JwtUserPrincipal;
import chemlab.shared.requests.CreateFlashcardRequest;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FlashcardController.class)
@Import(FlashcardControllerTest.SecurityPrincipalTestConfiguration.class)
class FlashcardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private FlashcardService flashcardService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private ProblemDetailFactory  problemDetailFactory;

    @Test
    @DisplayName("It should add the flashcard to the list of user flashcards")
    void addUserFlashcard() throws Exception {
        // Arrange
        // FlashcardDto flashcardDto = new FlashcardDto("12345", "Make me unique?", "yes");
        when(flashcardService.create(any(CreateFlashcardRequest.class)))
                .thenReturn(List.of(new Flashcard("Make me unique?", "yes")));

        // Act & Assert
        mockMvc.perform(post("/api/flashcards/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "userId": "12345",
                              "question": "Make me unique?",
                              "answer": "yes"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].answer").value("yes"))
                .andExpect(jsonPath("$[0].question")
                        .value("Make me unique?"));

        verify(flashcardService).create(any(CreateFlashcardRequest.class));
    }

    @Test
    @DisplayName("It should return a list of user flashcards when the userId is valid")
    void getUserFlashcards() throws Exception {
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        Claim userIdClaim = mock(Claim.class);
        when(jwtTokenProvider.verifyToken(eq("test-token"), anyString())).thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("test-user");
        when(decodedJWT.getClaim("userId")).thenReturn(userIdClaim);
        when(userIdClaim.asString()).thenReturn("12345");
        when(jwtTokenProvider.getAuthorities(decodedJWT))
                .thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));
        doCallRealMethod().when(jwtTokenProvider).getAuthentication(
                any(JwtUserPrincipal.class), anyList(), any(HttpServletRequest.class));

        when(flashcardService.listUserFlashcards("12345"))
                .thenReturn(List.of(new Flashcard("Make me unique?", "yes")));

        mockMvc.perform(get("/api/flashcards").header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].question").value("Make me unique?"))
                .andExpect(jsonPath("$[0].answer").value("yes"));

        verify(flashcardService).listUserFlashcards("12345");
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/flashcards/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": "",
                        "question": "",
                        "answer": ""
                    }
                """
                )).andExpect(status().isBadRequest());
        verifyNoInteractions(flashcardService);
    }

    @TestConfiguration
    static class SecurityPrincipalTestConfiguration implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
        }
    }
}
