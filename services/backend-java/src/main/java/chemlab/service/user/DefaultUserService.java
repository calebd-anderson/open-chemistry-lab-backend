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
import chemlab.shared.requests.CreateUserRequest;
import chemlab.shared.requests.UpdateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
        userProfileService.persistUserWithDuplicateCheck(user);

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
        userProfileService.persistUserWithDuplicateCheck(userToUpdate);
        return userToUpdate;
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
