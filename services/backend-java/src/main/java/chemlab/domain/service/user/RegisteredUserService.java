package chemlab.domain.service.user;

import chemlab.domain.exceptions.*;
import chemlab.domain.model.user.User;
import chemlab.shared.requests.UpdateUserRequest;
import org.springframework.web.multipart.MultipartFile;
import chemlab.shared.requests.RegisterUserRequest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RegisteredUserService {
    User register(RegisterUserRequest registerUserRequest) throws UserNotFoundException, UsernameExistException, EmailExistException;

    List<User> getUsers();

    Optional<User> findUserByUsername(String username);

    User addNewUser(String firstName,
                    String lastName,
                    String username,
                    String email,
                    String role,
                    boolean isNonLocked,
                    boolean isActive,
                    MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User updateUser(UpdateUserRequest updateUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException;

    byte[] getProfileImage(String userId, String filename) throws IOException;

    User updateProfileImage(String username,
                            MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void saveLastLogin(Date date, String username);
}
