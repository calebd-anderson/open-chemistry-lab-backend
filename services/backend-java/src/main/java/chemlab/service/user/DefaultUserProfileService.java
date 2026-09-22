package chemlab.service.user;

import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.NotAnImageFileException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.RegisteredUserRepository;
import chemlab.domain.service.user.UserProfileService;
import chemlab.infrastructure.storage.ImageStorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

@Service
@Log4j2
public class DefaultUserProfileService implements UserProfileService {
    @Autowired
    private RegisteredUserRepository userRepo;
    @Autowired
    private ImageStorageService imageStorageService;

    @Override
    public User updateProfileImage(String username, MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImg(user, profileImg);
        persistUserWithDuplicateCheck(user);
        return user;
    }

    @Override
    public void saveLastLogin(Date date, String username) {
        Optional<User> user = userRepo.findByUsername(username);
        user.ifPresent(value -> {
            value.setLastLoginDate(new Date());
            userRepo.save(value);
        });
    }

    public byte[] getProfileImage(String userId, String fileName) throws IOException {
        String blobPath = userId + "/" + fileName;
        return imageStorageService.getImage(blobPath);
    }


    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/user/image/robohash/" + username).toUriString();
    }


    private void saveProfileImg(User user, MultipartFile profileImg) throws IOException, NotAnImageFileException {
        if (profileImg != null) {
            // calculate file hash
            String md5Hash = createMD5HashImg(profileImg);
            String filename = md5Hash + "_" + user.getUsername();
            log.info("Image hash: {}", md5Hash);
            String imageBlobPath = imageStorageService.saveImage(user.getUserId(), filename + ".jpg", profileImg.getInputStream());
            String profileImageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/user/image/" + imageBlobPath).toUriString();
            user.setProfileImgUrl(profileImageUrl);
            log.trace("Successfully updated user profile image.");
        }
    }

    private String createMD5HashImg(final MultipartFile input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Compute message digest of the input
            byte[] messageDigest = md.digest(input.getBytes());
            return convertToHex(messageDigest);
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private String convertToHex(final byte[] messageDigest) {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }
}
