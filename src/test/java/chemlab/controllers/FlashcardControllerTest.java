package chemlab.controllers;

import chemlab.auth.jwt.JwtTokenProvider;
import chemlab.controller.api.game.FlashcardController;
import chemlab.domain.game.FlashcardService;
import chemlab.model.game.Flashcard;
import chemlab.model.shared.FlashcardDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FlashcardController.class)
class FlashcardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private FlashcardService flashcardService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("It should insert the flashcard into the db")
//    @WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
//    @WithMockUser(username = "alice")
    void testCreate() throws Exception {
        // Arrange

        // FlashcardDto flashcardDto = new FlashcardDto("12345", "Make me unique?", "yes");

        when(flashcardService.create(any(FlashcardDto.class)))
                .thenReturn(List.of(new Flashcard("Make me unique?", "yes")));

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

        verify(flashcardService).create(any(FlashcardDto.class));
    }

//    @Test
//    @DisplayName("It should request findByQuestion when queryQuestion called")
//    void testQueryQuestion() {
//        flashcardController.queryQuestions(question);
//        verify(flashcardRepo, times(1)).findByQuestion(question);
//    }
//
//    @Test
//    @DisplayName("It should return a list of questions from the db")
//    void testQueryAnswers() {
//        flashcardController.queryAnswers(answer);
//        verify(flashcardRepo, times(1)).findByAnswer(answer);
//    }
}
