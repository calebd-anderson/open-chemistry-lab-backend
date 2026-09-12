package chemlab.controllers.api;

import chemlab.security.jwt.JwtTokenProvider;
import chemlab.presentation.api.game.FlashcardController;
import chemlab.domain.service.game.FlashcardService;
import chemlab.domain.model.game.Flashcard;
import chemlab.shared.requests.CreateFlashcardRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FlashcardController.class)
class FlashcardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private FlashcardService flashcardService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

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
        when(flashcardService.listUserFlashcards("12345"))
                .thenReturn(List.of(new Flashcard("Make me unique?", "yes")));

        mockMvc.perform(get("/api/flashcards/userflashcards/12345"))
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
}
