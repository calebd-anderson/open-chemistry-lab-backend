package chemlab.controllers.api;

import chemlab.domain.service.user.RegisteredUserService;
import chemlab.domain.model.user.User;
import chemlab.infrastructure.fastapiworker.service.FastApiWorkerService;
import chemlab.infrastructure.persistence.user.RegisteredUserPersistenceAdapter;
import chemlab.infrastructure.persistence.user.UserReactionPersistenceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
public class UserControllerTest {
    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private RegisteredUserService registeredUserService;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    FastApiWorkerService fastApiWorkerService;
    @MockitoBean
    RegisteredUserPersistenceAdapter registeredUserPersistenceAdapter;
    @MockitoBean
    UserReactionPersistenceAdapter userReactionPersistenceAdapter;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).apply(springSecurity()).build();
        given(this.registeredUserService.getUsers()).willReturn(List.of(new User("jimbo", "jimbo@mail.com")));
    }

    @Test
    @WithMockUser(roles = "USER", authorities = {"user:read"})
    @DisplayName("role USER not authorized to enumerate all users")
    public void enumerateUsersFail() throws Exception {
        // https://auth0.com/blog/forbidden-unauthorized-http-status-codes/
        // this should be updated to return a 401 with details, see above link
        // src/main/java/chemlab/auth/config/SecurityConfig.java
        // .requestMatchers("/api/user/list").hasAnyRole("ADMIN", "SUPER_ADMIN")
        // adding a requestMatchers() to this path and mocking a user does give a 401
        // requires more research
        // https://stackoverflow.com/questions/30643029/spring-security-anonymous-401-instead-of-403
        // https://basicutils.com/learn/spring-security/implementing-role-based-access-control-rbac-spring-boot
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/list"))
                .andExpect(status().isForbidden())
                .andExpect(authenticated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("role ADMIN authorized to enumerate all users")
    void shouldReturnAllUsers() throws Exception {
        this.mockMvc.perform(get("/api/user/list")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(authenticated())
                .andDo(print());
        verify(registeredUserService).getUsers();
    }
}
