package chemlab.service.user;

import chemlab.domain.exceptions.*;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.RegisteredUserRepository;
import chemlab.domain.service.user.RegisteredUserService;
import chemlab.infrastructure.email.EmailService;
import chemlab.infrastructure.storage.ImageStorageService;
import chemlab.security.user.LoginAttemptService;
import chemlab.security.user.RegisteredUserPrincipal;
import chemlab.security.user.Role;
import chemlab.shared.requests.RegisterUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static chemlab.security.user.Role.ROLE_USER;
import static chemlab.service.user.config.UserImplementationConstant.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Slf4j
public class DefaultUserService implements RegisteredUserService, UserDetailsService {

    @Autowired
    private RegisteredUserRepository userRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ImageStorageService imageStorageService;

    @Override
    public User register(RegisterUserRequest userDto) throws UserNotFoundException, UsernameExistException, EmailExistException {
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

    public Optional<User> findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username);
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
//        emailService.sendNewPasswordEmail(firstName, password, email);
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
        Optional<User> user = userRepo.findByUsername(username);
        user.ifPresent(value -> {
            value.setLastLoginDate(new Date());
            userRepo.save(value);
        });
    }

    @Override
    public void deleteUser(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        user.ifPresent(value -> {

            userRepo.deleteById(value.getId());
        });
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.get().setPassword(encodePassword(password));
        userRepo.save(user.get());
//		log.info("New user password: " + password);
//        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
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
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isEmpty()) {
            log.error(NO_USER_FOUND_BY_USERNAME + "{}", username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user.get());
            log.info("user: {} found in the database", username);
        }
        return new RegisteredUserPrincipal(user.get());
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        // Only look up by newUsername/newEmail if they are provided to avoid unnecessary DB calls and NPEs
        Optional<User> userByNewUsername = StringUtils.isNotBlank(newUsername) ? findUserByUsername(newUsername) : Optional.empty();
        Optional<User> userByNewEmail = StringUtils.isNotBlank(newEmail) ? findUserByEmail(newEmail) : Optional.empty();

        if (StringUtils.isNotBlank(currentUsername)) {
            // Update scenario: ensure the current user exists and any found user for the new
            // username/email is either null or the same as the current user
            Optional<User> currentUser = findUserByUsername(currentUsername);
            if (currentUser.isEmpty()) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername.isPresent() && !currentUser.get().getId().equals(userByNewUsername.get().getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail.isPresent() && !currentUser.get().getId().equals(userByNewEmail.get().getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser.get();
        } else {
            // Create scenario: new username/email must not already exist
            if (userByNewUsername.isPresent()) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail.isPresent()) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
        }
        return null;
    }

    private User validateEditUsernameAndEmail(String userId, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        Optional<User> userByNewUsername = findUserByUsername(newUsername);
        Optional<User> userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(userId)) {
            Optional<User> currentUser = findUserByUserId(userId);
            if (currentUser.isEmpty()) {
                throw new UserNotFoundException("No user found by id: " + userId);
            }
            log.info(currentUser.get().getUserId());
            if (userByNewUsername.isPresent() && !currentUser.get().getId().equals(userByNewUsername.get().getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail.isPresent() && !currentUser.get().getId().equals(userByNewEmail.get().getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser.get();
        } else {
            if (userByNewUsername.isPresent()) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail.isPresent()) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
        }
        return null;
    }

    private Optional<User> findUserByUserId(String userId) {
        return userRepo.findByUserId(userId);
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
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/user/image/robohash/" + username).toUriString();
    }

    private String generateUserId() {
        // return secure random number length 10
        return RandomStringUtils.secure().next(10, false, true);
    }

    private String generatePassword() {
        return RandomStringUtils.secure().nextAlphanumeric(10);
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private void saveProfileImg(User user, MultipartFile profileImg) throws IOException, NotAnImageFileException {
        if (profileImg != null) {
            // calculate file hash
            String md5Hash = createMD5HashImg(profileImg);
            String filename = md5Hash + "_" + user.getUsername();
            log.info("image hash: {}", md5Hash);
            String imageBlobPath = imageStorageService.saveImage(user.getUserId(), filename + ".jpg", profileImg.getInputStream());
            ServletUriComponentsBuilder.fromCurrentContextPath().path("api/user/image/"+ imageBlobPath).toUriString();
            userRepo.save(user);
            log.trace("Successfully updated user profile image.");
        }
    }

    public byte[] getProfileImage(String userId, String fileName) throws IOException {
        String blobPath = userId + "/" + fileName;
        return imageStorageService.getImage(blobPath);
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
