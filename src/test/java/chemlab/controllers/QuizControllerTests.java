package chemlab.controllers;

import chemlab.auth.jwt.JwtTokenProvider;
import chemlab.controller.api.game.QuizController;
import chemlab.domain.game.QuizService;
import chemlab.model.chemistry.Reaction;
import chemlab.model.game.UserQuiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = QuizController.class)
public class QuizControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizService quizService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;


    @Test
    void shouldReturnUserQuizzes() throws Exception {
        // Arrange
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        Reaction r1 = new Reaction(elements);

        UserQuiz userQuiz = new UserQuiz("Is this correct?", "no");

        when(quizService.findQuizByUserId("12345"))
                .thenReturn(List.of(userQuiz));

        // Act
        this.mockMvc
                .perform(get("/api/quiz/getbyuserid/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.size()").value(1))
                .andDo(print());

        // Assert
        verify(quizService).findQuizByUserId("12345");
    }
}
