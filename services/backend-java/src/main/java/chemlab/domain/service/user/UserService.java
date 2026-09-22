package chemlab.domain.service.user;

import chemlab.domain.exceptions.*;
import chemlab.domain.model.user.User;
import chemlab.shared.requests.CreateUserRequest;
import chemlab.shared.requests.UpdateUserRequest;
import org.springframework.web.multipart.MultipartFile;
import chemlab.shared.requests.RegisterUserRequest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getUsers();

    Optional<User> findUserByUsername(String username);

    void saveUser(User user);

    User addNewUser(CreateUserRequest createUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User updateUser(UpdateUserRequest updateUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;
}
