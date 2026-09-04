package chemlab.domain.service.user;

import chemlab.domain.exceptions.*;
import chemlab.domain.model.user.User;
import org.springframework.web.multipart.MultipartFile;
import chemlab.shared.UserRegisterRequest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface RegisteredUserService {
    User register(UserRegisterRequest userRegisterRequest) throws UserNotFoundException, UsernameExistException, EmailExistException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User addNewUser(String firstName,
                    String lastName,
                    String username,
                    String email,
                    String role,
                    boolean isNonLocked,
                    boolean isActive,
                    MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User updateUser(String currentUsername,
                    String newFirstName,
                    String newLastName,
                    String newUsername,
                    String newEmail,
                    String role,
                    boolean isNonLocked,
                    boolean isActive,
                    MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User editUser(String userId,
                  String newFirstName,
                  String newLastName,
                  String newUsername,
                  String newEmail,
                  String role,
                  boolean isNonLocked,
                  boolean isActive,
                  MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException;

    byte[] getProfileImage(String userId, String filename) throws IOException;

    User updateProfileImage(String username,
                            MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void saveLastLogin(Date date, String username);
}
