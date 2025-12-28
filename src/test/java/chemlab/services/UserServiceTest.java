package chemlab.services;

import auth.user.LoginAttemptService;
import chemlab.domain.user.RegisteredUserService;
import chemlab.repository.user.RegisteredUserRepository;
import chemlab.service.user.RegisteredUserServiceImpl;
import infrastructure.email.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private RegisteredUserRepository userRepo;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private LoginAttemptService loginAttemptService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private RegisteredUserService userService = new RegisteredUserServiceImpl();

    @Test
    void findQuizByUserId() {
        assert (true);
//        List<Quiz> quizzes = quizService.findQuizByUserId();
//        verify(quizRepo, atLeastOnce()).findAllById((List<ObjectId>) any());
    }
}
