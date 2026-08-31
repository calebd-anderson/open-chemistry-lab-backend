package chemlab.controllers.api;

import chemlab.auth.jwt.JwtTokenProvider;
import chemlab.controller.api.chemistry.ReactionController;
import chemlab.domain.chemistry.ReactionService;
import chemlab.model.chemistry.Element;
import chemlab.model.chemistry.Reaction;
import chemlab.model.shared.ReactionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("It should submit the reaction to PubChem and respond with the validated reaction")
    void validateReaction() throws Exception {
        // Arrange
        ArrayList<Element> elementList = new ArrayList<>() {{
            add(new Element("H", 2));
            add(new Element("O", 1));
        }};

        ReactionRequest reactionRequest = new ReactionRequest(elementList, "12345");
        Reaction reaction = new Reaction(reactionRequest.getMappedPayload());

        when(reactionService.validateInput(any(Reaction.class)))
                .thenReturn(reaction);

        // Serialize ReactionRequest to JSON string
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(reactionRequest);

        // Act & Assert
        mockMvc.perform(post("/api/compound/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formula").value("H2O"));

        verify(reactionService).validateInput(any(Reaction.class));
    }
}
