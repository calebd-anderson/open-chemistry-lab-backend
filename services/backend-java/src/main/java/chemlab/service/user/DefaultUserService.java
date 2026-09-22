package chemlab.service.user;

import chemlab.config.CustomMapper;
import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.NotAnImageFileException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.RegisteredUserRepository;
import chemlab.domain.service.user.UserAuthenticationService;
import chemlab.domain.service.user.UserProfileService;
import chemlab.domain.service.user.UserRegistrationService;
import chemlab.domain.service.user.UserService;
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
public class DefaultUserService implements UserService {

    @Autowired
    private RegisteredUserRepository userRepo;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Autowired
    private UserRegistrationService userRegistrationService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    CustomMapper customMapper;

    public List<User> getUsers() {
        log.trace("Fetching all users.");
        return userRepo.findAll();
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User addNewUser(CreateUserRequest createUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        userValidator.validateNewUsernameAndEmail(EMPTY, createUserRequest.getUsername(), createUserRequest.getEmail());

        User user = userRegistrationService.buildUserEntity(
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getUsername(),
                createUserRequest.getEmail(),
                userAuthenticationService.generatePassword(),
                createUserRequest.getRole(),
                createUserRequest.getProfileImg()
        );

        customMapper.createUserFromDto(createUserRequest, user);
        persistUserWithDuplicateCheck(user);

        return user;
    }

    @Override
    public User updateUser(UpdateUserRequest updateUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException {
        // Validate uniqueness using userId (single DB lookup inside validateEditUsernameAndEmail)
        User userToUpdate = userValidator.validateEditUsernameAndEmail(updateUserRequest.userId, updateUserRequest.username, updateUserRequest.email);
        customMapper.updateUserFromDto(updateUserRequest, userToUpdate);
        try {
            if (updateUserRequest.profileImg != null && !updateUserRequest.profileImg.isEmpty()) {
                userProfileService.saveProfileImg(userToUpdate, updateUserRequest.profileImg);
            }
        } catch (IOException e) {
//        } catch (IOException | NotAnImageFileException e) {
            throw new RuntimeException(e);
        }
        persistUserWithDuplicateCheck(userToUpdate);
        return userToUpdate;
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

    @Override
    public void deleteUser(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        user.ifPresent(value -> {
            String[] imageUrlParts = value.getProfileImgUrl().split("/");
            String part = imageUrlParts[imageUrlParts.length - 2];
            if (!(part.equals("robohash"))) {
                String imageSlug = imageUrlParts[imageUrlParts.length - 1];
                userProfileService.deleteProfileImage(imageSlug);
            }
            userRepo.deleteById(value.getId());
        });
    }
}
