package chemlab.controllers.view;

import chemlab.infrastructure.persistence.user.UserPersistenceAdapter;
import chemlab.infrastructure.persistence.user.UserReactionPersistenceAdapter;
import chemlab.security.config.CorsProperties;
import chemlab.infrastructure.fastapiworker.service.FastApiWorkerService;
import chemlab.service.chemistry.DefaultElementService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@WebAppConfiguration
//@ActiveProfiles(profiles = "dev")
class PubChemElementViewControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;

    @MockitoBean
    private DefaultElementService elmServiceMock;
    @MockitoBean
    private CorsProperties corsProperties;
    @MockitoBean
    FastApiWorkerService fastApiWorkerService;
    @MockitoBean
    UserPersistenceAdapter userPersistenceAdapter;
    @MockitoBean
    UserReactionPersistenceAdapter userReactionPersistenceAdapter;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testCheckControllerExists() {
        assertThat(applicationContext.containsBean("elementViewController")).isTrue();
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
}
