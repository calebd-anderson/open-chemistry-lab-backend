package chemlab.domain.user;

import chemlab.domain.model.user.User;
import chemlab.exceptions.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface RegisteredUserService {
    User register(String firstName,
                  String lastName,
                  String username,
                  String password,
                  String email) throws UserNotFoundException, UsernameExistException, EmailExistException;

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

    void deleteUser(String id) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException;

    User updateProfileImage(String username,
                            MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void saveLastLogin(Date date, String username);
}
