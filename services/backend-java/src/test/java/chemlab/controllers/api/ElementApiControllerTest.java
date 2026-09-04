package chemlab.controllers.api;

import chemlab.auth.jwt.JwtTokenProvider;
import chemlab.presentation.api.chemistry.ElementController;
import chemlab.domain.service.chemistry.ElementService;
import chemlab.infrastructure.pubchem.PubChemElement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ElementController.class)
class ElementApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ElementService elmServiceMock;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void testList() throws Exception {
        assertThat(this.elmServiceMock).isNotNull();
        assertThat(this.mockMvc).isNotNull();
        // Arrange
        when(elmServiceMock.getAllElements())
                .thenReturn(List.of(new PubChemElement()));

        // Act & Assert
        mockMvc.perform(get("/api/elements/list")
//                        .header("Authorization", "Bearer null")
//                        .header("Access-Control-Request-Method", "GET")
//                        .header("Origin", "http://localhost:4200/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

        verify(elmServiceMock).getAllElements();
    }

//    @Test
//    public void csrfValidate() throws Exception {
//        mockMvc.perform(post("/").with(csrf()));
//    }
//
//    @Test
//    public void invalidCsrf() throws Exception {
//        mockMvc.perform(post("/").with(csrf().useInvalidToken()));
//    }
}
