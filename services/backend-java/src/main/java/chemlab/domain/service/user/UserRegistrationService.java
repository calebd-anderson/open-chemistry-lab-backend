package chemlab.domain.service.user;

import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.shared.requests.RegisterUserRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserRegistrationService {
    User register(RegisterUserRequest registerUserRequest) throws UserNotFoundException, UsernameExistException, EmailExistException;
    User buildUserEntity(String firstName,
                         String lastName,
                         String username,
                         String email,
                         String password,
                         String roleName,
                         MultipartFile profileImg);
}
