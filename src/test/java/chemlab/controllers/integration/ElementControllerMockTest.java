package chemlab.controllers.integration;

import auth.CorsProperties;
import chemlab.service.chemistry.ElementService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@WebAppConfiguration
@ActiveProfiles(profiles = "dev")
class ElementControllerMockTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ElementService elmServiceMock;
    @MockitoBean
    private CorsProperties corsProperties;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testListView() throws Exception {
        assertThat(this.elmServiceMock).isNotNull();
        mockMvc.perform(get("/elements"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("elements"))
                .andExpect(MockMvcResultMatchers.view().name("elements"))
                .andExpect(content().string(Matchers.containsString("Welcome to Elements Page")))
                .andDo(print());
    }

    @Test
    public void testList() throws Exception {
        assertThat(this.elmServiceMock).isNotNull();
        assertThat(this.mockMvc).isNotNull();
        mockMvc.perform(get("/elements/list")
                        .header("Authorization", "Bearer null")
                        .header("Access-Control-Request-Method", "GET")
//                        .header("Origin", "http://localhost:4200/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());
    }

    @Test
    public void csrfValidate() throws Exception {
        mockMvc.perform(post("/").with(csrf()));
    }

    @Test
    public void invalidCsrf() throws Exception {
        mockMvc.perform(post("/").with(csrf().useInvalidToken()));
    }
}
