package chemlab.controller.api;

import chemlab.controller.api.chemistry.ReactionController;
import chemlab.domain.model.chemistry.Reaction;
import chemlab.domain.service.chemistry.ReactionService;
import chemlab.domain.service.ml.UnsupervisedClustMap;
import chemlab.security.jwt.JwtTokenProvider;
import chemlab.shared.requests.Element;
import chemlab.shared.requests.ReactionRequest;
import chemlab.shared.responses.ReactionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReactionController.class)
public class ReactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ReactionService reactionService;
    @MockitoBean
    UnsupervisedClustMap unsupervisedClustMap;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("It should submit the reaction to PubChem and respond with the validated reaction")
    void validateReaction() throws Exception {
        // Arrange
        ArrayList<Element> elementList = new ArrayList<>() {{
            add(new Element("H", 2));
            add(new Element("O", 1));
        }};

        ReactionRequest reactionRequest = new ReactionRequest(elementList);
        Reaction reaction = new Reaction(reactionRequest.getMappedPayload());

        ModelMapper modelMapper = new ModelMapper();
        ReactionResponse response = modelMapper.map(reaction, ReactionResponse.class);

        when(reactionService.createReaction(any(ReactionRequest.class), any()))
                .thenReturn(response);

        // Serialize ReactionRequest to JSON string
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(reactionRequest);

        // Act & Assert
        mockMvc.perform(post("/api/compound/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formula").value("H2O"));

        verify(reactionService).createReaction(any(ReactionRequest.class), any());
    }

    @Test
    @DisplayName("It should handle the authenticated user path and pass the username to the service")
    void validateReactionAsAuthenticatedUser() throws Exception {
        // Arrange
        ArrayList<Element> elementList = new ArrayList<>() {{
            add(new Element("H", 2));
            add(new Element("O", 1));
        }};
        ReactionRequest reactionRequest = new ReactionRequest(elementList);
        Reaction reaction = new Reaction(reactionRequest.getMappedPayload());

        ModelMapper modelMapper = new ModelMapper();
        ReactionResponse response = modelMapper.map(reaction, ReactionResponse.class);

        // Match the new signature: (Request, Optional<String>)
        when(reactionService.createReaction(any(ReactionRequest.class), any()))
                .thenReturn(response);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(reactionRequest);

        // Act & Assert
        mockMvc.perform(post("/api/compound/validate") // Note: ensure path matches your Controller
                        .with(user("test_user"))   // This simulates an authenticated user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formula").value("H2O"));

        // Verify that the service was called (the 'user' helper handles the String -> Optional conversion)
        verify(reactionService).createReaction(any(ReactionRequest.class), any());
    }
}
