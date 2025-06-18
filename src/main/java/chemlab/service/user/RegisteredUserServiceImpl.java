package chemlab.service.user;

import auth.user.LoginAttemptService;
import auth.user.RegisteredUserPrincipal;
import auth.user.Role;
import chemlab.model.user.User;
import chemlab.repository.user.RegisteredUserRepository;
import chemlab.domain.user.RegisteredUserService;
import chemlab.exceptions.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import infrastructure.email.EmailService;
import shared.UserRegisterDto;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static auth.user.Role.ROLE_USER;
import static chemlab.service.user.config.FileConstants.*;
import static chemlab.service.user.UserImplementationConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;

@Service
@Slf4j
public class RegisteredUserServiceImpl implements RegisteredUserService, UserDetailsService {

    @Autowired
    private RegisteredUserRepository userRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private EmailService emailService;

    @Override
    public User register(UserRegisterDto userDto) throws UserNotFoundException, UsernameExistException, EmailExistException {
        validateNewUsernameAndEmail(EMPTY, userDto.getUsername(), userDto.getEmail());
        User user = new User();
        user.setUserId(generateUserId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(userDto.getPassword()));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImgUrl(getTemporaryProfileImageUrl(userDto.getUsername()));
        userRepo.save(user);
        return user;
    }

    public User findUserByEmail(String email) {
        return userRepo.findRegisteredUserByEmail(email);
    }

    public User findUserByUsername(String username) {
        return userRepo.findRegisteredUserByUsername(username);
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImgUrl(getTemporaryProfileImageUrl(username));
        userRepo.save(user);
        saveProfileImg(user, profileImg);
//		log.info("New user password: " + password);
        emailService.sendNewPasswordEmail(firstName, password, email);
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User user = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        // role is undefined from form in user space, so leave these properties alone
        if (!role.equalsIgnoreCase("undefined")) {
            user.setActive(isActive);
            user.setNotLocked(isNonLocked);
            user.setRole(getRoleEnumName(role).name());
            user.setAuthorities(getRoleEnumName(role).getAuthorities());
        }
        userRepo.save(user);
        saveProfileImg(user, profileImg);
        return user;
    }

    @Override
    public User editUser(String userId, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User user = validateEditUsernameAndEmail(userId, newUsername, newEmail);
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepo.save(user);
        saveProfileImg(user, profileImg);
        return user;
    }

    public void saveLastLogin(Date date, String username) {
        User user = userRepo.findRegisteredUserByUsername(username);
        user.setLastLoginDate(new Date());
        userRepo.save(user);
    }

    @Override
    public void deleteUser(String username) throws IOException {
        User user = userRepo.findRegisteredUserByUsername(username);
        Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepo.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user = userRepo.findRegisteredUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepo.save(user);
//		log.info("New user password: " + password);
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImg(user, profileImg);
        return user;
    }

    public List<User> getUsers() {
        log.info("fetching all users");
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findRegisteredUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            log.info("user: {} found in the database", username);
        }
        return new RegisteredUserPrincipal(user);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getUsername().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getEmail().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
        }
        return null;
    }

    private User validateEditUsernameAndEmail(String userId, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(userId)) {
            User currentUser = findUserByUserId(userId);
            log.info(currentUser.getUserId());
            if (currentUser == null) {
                throw new UserNotFoundException("No user found by id: " + userId);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
        }
        return null;
    }

    private User findUserByUserId(String userId) {
        return userRepo.findRegisteredUserByUserId(userId);
    }

    // update 500 instead of user not found ?
    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String generateUserId() {
        // return random number length 10
        return RandomStringUtils.secure().next(10);
    }

    private String generatePassword() {
        return RandomStringUtils.secure().nextAlphanumeric(10);
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private void saveProfileImg(User user, MultipartFile profileImg) throws IOException, NotAnImageFileException {
        if (profileImg != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImg.getContentType())) {
                throw new NotAnImageFileException(profileImg.getOriginalFilename() + " is not an image file. Please upload an image.");
            }
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            }
            // calculate file hash
            String md5Hash = createMD5HashImg(profileImg);
            String filename = md5Hash + "_" + user.getUsername();
//			Files.deleteIfExists(Paths.get(userFolder + filename + DOT + JPG_EXTENSION));
            log.info("image hash: " + md5Hash);
            Files.copy(profileImg.getInputStream(), userFolder.resolve(filename + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImgUrl(setProfileImgURL(user.getUsername(), md5Hash));
            userRepo.save(user);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImg.getOriginalFilename());
        }
    }

    private String setProfileImgURL(String username, String hash) {
        log.info("image hash: " + hash);
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
                + hash + "_" + username + DOT + JPG_EXTENSION).toUriString();
    }

    private String createMD5HashImg(final MultipartFile input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Compute message digest of the input
            byte[] messageDigest = md.digest(input.getBytes());
            return convertToHex(messageDigest);
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error(e.getMessage());
//            e.printStackTrace();
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
