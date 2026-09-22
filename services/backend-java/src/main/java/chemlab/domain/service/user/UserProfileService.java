package chemlab.domain.service.user;

import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.NotAnImageFileException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

public interface UserProfileService {
    byte[] getProfileImage(String userId, String filename) throws IOException;

    User updateProfileImage(String username,
                            MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void saveLastLogin(Date date, String username);

    void deleteProfileImage(String username);

    void saveProfileImg(User user, MultipartFile profileImg) throws IOException;
}
