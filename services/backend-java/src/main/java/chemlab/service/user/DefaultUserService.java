package chemlab.service.user;

import chemlab.config.CustomMapper;
import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.NotAnImageFileException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.RegisteredUserRepository;
import chemlab.domain.service.user.RegisteredUserService;
import chemlab.infrastructure.email.EmailService;
import chemlab.infrastructure.storage.ImageStorageService;
import chemlab.security.user.LoginAttemptService;
import chemlab.security.user.RegisteredUserPrincipal;
import chemlab.security.user.Role;
import chemlab.shared.requests.CreateUserRequest;
import chemlab.shared.requests.RegisterUserRequest;
import chemlab.shared.requests.UpdateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
    @Autowired
    CustomMapper customMapper;

    @Override
    public User register(RegisterUserRequest userDto) throws UserNotFoundException, UsernameExistException, EmailExistException {
        validateNewUsernameAndEmail(EMPTY, userDto.getUsername(), userDto.getEmail());
        try {
            User user = buildUserEntity(
                    userDto.getFirstName(),
                    userDto.getLastName(),
                    userDto.getUsername(),
                    userDto.getEmail(),
                    userDto.getPassword(),
                    ROLE_USER.name(),
                    null
            );
            persistUserWithDuplicateCheck(user);
            return user;
        } catch (IOException | NotAnImageFileException e) {
            throw new IllegalStateException("Unable to create user during registration.", e);
        }
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User addNewUser(CreateUserRequest createUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        validateNewUsernameAndEmail(EMPTY, createUserRequest.getUsername(), createUserRequest.getEmail());

        User user = buildUserEntity(
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getUsername(),
                createUserRequest.getEmail(),
                generatePassword(),
                createUserRequest.getRole(),
                createUserRequest.getProfileImg()
        );

        customMapper.createUserFromDto(createUserRequest, user);
        persistUserWithDuplicateCheck(user);

        return user;
    }

    private User buildUserEntity(String firstName,
                                String lastName,
                                String username,
                                String email,
                                String password,
                                String roleName,
                                MultipartFile profileImg) throws IOException, NotAnImageFileException {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);

        Role resolvedRole = StringUtils.isNotBlank(roleName) ? getRoleEnumName(roleName) : ROLE_USER;
        user.setRole(resolvedRole.name());
        user.setAuthorities(resolvedRole.getAuthorities());

        if (profileImg != null && !profileImg.isEmpty()) {
            saveProfileImg(user, profileImg);
        } else {
            user.setProfileImgUrl(getTemporaryProfileImageUrl(username));
        }

        return user;
    }

    @Override
    public User updateUser(UpdateUserRequest updateUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException {
        // Validate uniqueness using userId (single DB lookup inside validateEditUsernameAndEmail)
        User userToUpdate = validateEditUsernameAndEmail(updateUserRequest.userId, updateUserRequest.username, updateUserRequest.email);
        customMapper.updateUserFromDto(updateUserRequest, userToUpdate);
        try {
            if (updateUserRequest.profileImg != null && !updateUserRequest.profileImg.isEmpty()) {
                saveProfileImg(userToUpdate, updateUserRequest.profileImg);
            }
        } catch (IOException | NotAnImageFileException e) {
            throw new RuntimeException(e);
        }
        persistUserWithDuplicateCheck(userToUpdate);
        return userToUpdate;
    }

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

    @Override
    public void resetPassword(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepo.save(user);
    }

    public List<User> getUsers() {
        log.trace("Fetching all users.");
        return userRepo.findAll();
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isEmpty()) {
            log.error(NO_USER_FOUND_BY_USERNAME + "{}", username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user.get());
            log.info("User: {} found in the database.", username);
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
            Optional<User> currentUserOpt = findUserByUsername(currentUsername);
            if (currentUserOpt.isEmpty()) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            User currentUser = currentUserOpt.get();

            if (userByNewUsername.isPresent() && !currentUser.getId().equals(userByNewUsername.get().getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail.isPresent() && !currentUser.getId().equals(userByNewEmail.get().getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            // Create scenario: new username/email must not already exist
            if (userByNewUsername.isPresent()) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail.isPresent()) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private User validateEditUsernameAndEmail(String userId, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        // Resolve the current user once (by id) and then perform uniqueness checks against
        // the new username/email — this avoids an extra lookup of the current user by username.
        User currentUser = findUserByUserId(userId).orElseThrow(() -> new UserNotFoundException("No user found by id: " + userId));

        Optional<User> userByNewUsername = StringUtils.isNotBlank(newUsername) ? findUserByUsername(newUsername) : Optional.empty();
        Optional<User> userByNewEmail = StringUtils.isNotBlank(newEmail) ? findUserByEmail(newEmail) : Optional.empty();

        if (userByNewUsername.isPresent() && !currentUser.getId().equals(userByNewUsername.get().getId())) {
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        if (userByNewEmail.isPresent() && !currentUser.getId().equals(userByNewEmail.get().getId())) {
            throw new EmailExistException(EMAIL_ALREADY_EXISTS);
        }
        return currentUser;
    }

    private void persistUserWithDuplicateCheck(User user) throws UsernameExistException, EmailExistException {
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

    private String generatePassword() {
        return RandomStringUtils.secure().nextAlphanumeric(10);
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/user/image/robohash/" + username).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
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
    public void deleteUser(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        user.ifPresent(value -> {
            String[] imageUrlParts = value.getProfileImgUrl().split("/");
            String part = imageUrlParts[imageUrlParts.length - 2];
            if (!(part.equals("robohash"))) {
                String imageSlug = imageUrlParts[imageUrlParts.length - 1];
                imageStorageService.deleteImage(imageSlug);
            }
            userRepo.deleteById(value.getId());
        });
    }
}
