package chemlab.controllers.integration;

import chemlab.model.User;
import chemlab.services.user.RegisteredUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisteredUserControllerMockTest {
    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private RegisteredUserService registeredUserService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
        given(this.registeredUserService.getUsers()).willReturn(
                List.of(new User("jimbo", "jimbo@mail.com")));
    }

    @Test
    @WithMockUser(roles = "USER", authorities = {"user:read", "user:create", "user:delete"})
    @DisplayName("role USER not authorized enumerate all users")
    public void enumerateUsersFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/list"))
                .andExpect(status().isUnauthorized())
                .andExpect(authenticated());
    }

    @Test
    @WithMockUser(authorities = {"user:read", "user:create", "user:update"})
    @DisplayName("authority user:update is able to enumerate all users")
    public void enumerateUsersSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(authenticated());
    }

    @Test
    @WithMockUser(authorities = "user:update")
    @DisplayName("enumerate all users contains result")
    void shouldReturnAllUsers() throws Exception {
        this.mockMvc.perform(get("/user/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("jimbo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("jimbo@mail.com"))
                .andDo(print());
        verify(registeredUserService).getUsers();
    }
}
