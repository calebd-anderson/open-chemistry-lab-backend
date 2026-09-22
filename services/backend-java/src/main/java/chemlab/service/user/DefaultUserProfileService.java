package chemlab.service.user;

import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.NotAnImageFileException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.UserRepository;
import chemlab.domain.service.user.UserProfileService;
import chemlab.infrastructure.storage.ImageStorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

import static chemlab.service.user.config.UserImplementationConstant.EMAIL_ALREADY_EXISTS;
import static chemlab.service.user.config.UserImplementationConstant.USERNAME_ALREADY_EXISTS;

@Service
@Log4j2
public class DefaultUserProfileService implements UserProfileService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    UserValidator userValidator;

    @Override
    public User updateProfileImage(String username, MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User user = userValidator.validateNewUsernameAndEmail(username, null, null);
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


    public String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/users/image/robohash/" + username).toUriString();
    }


    public void saveProfileImg(User user, MultipartFile profileImg) throws IOException {
        if (profileImg != null) {
            // calculate file hash
            String md5Hash = createMD5HashImg(profileImg);
            String filename = md5Hash + "_" + user.getUsername();
            log.info("Image hash: {}", md5Hash);
            String imageBlobPath = imageStorageService.saveImage(user.getUserId(), filename + ".jpg", profileImg.getInputStream());
            String profileImageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/users/image/" + imageBlobPath).toUriString();
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

    @Override
    public void deleteProfileImage(String username) {
        imageStorageService.deleteImage(username);
    }

    public void persistUserWithDuplicateCheck(User user) throws UsernameExistException, EmailExistException {
        try {
            userRepo.save(user);
        } catch (DuplicateKeyException ex) {
            handleDuplicateKeyException(ex);
        } catch (DataIntegrityViolationException ex) {
            handleDuplicateKeyException(ex);
        }
    }
    private void handleDuplicateKeyException(Throwable ex) throws UsernameExistException, EmailExistException {
        String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        if (message != null && (message.contains("username"))) {
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        if (message != null && (message.contains("email"))) {
            throw new EmailExistException(EMAIL_ALREADY_EXISTS);
        }
        throw new IllegalStateException("Unique user constraint violation while saving user.", ex);
    }
}
