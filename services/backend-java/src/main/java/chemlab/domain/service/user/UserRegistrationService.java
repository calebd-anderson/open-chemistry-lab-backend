package chemlab.domain.service.user;

import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.NotAnImageFileException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.shared.requests.CreateUserRequest;
import chemlab.shared.requests.RegisterUserRequest;
import chemlab.shared.requests.UpdateUserRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserRegistrationService {
    User register(RegisterUserRequest registerUserRequest) throws UserNotFoundException, UsernameExistException, EmailExistException;
    User addNewUser(CreateUserRequest createUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;
    User updateUser(UpdateUserRequest updateUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;
    void deleteUser(String username) throws IOException;
}
