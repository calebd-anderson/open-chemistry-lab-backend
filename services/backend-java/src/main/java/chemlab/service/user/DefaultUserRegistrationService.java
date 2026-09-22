package chemlab.service.user;

import chemlab.config.CustomMapper;
import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.model.user.User;
import chemlab.domain.service.user.UserAuthenticationService;
import chemlab.domain.service.user.UserProfileService;
import chemlab.domain.service.user.UserRegistrationService;
import chemlab.domain.service.user.UserService;
import chemlab.infrastructure.email.EmailService;
import chemlab.security.user.Role;
import chemlab.shared.requests.CreateUserRequest;
import chemlab.shared.requests.RegisterUserRequest;
import chemlab.shared.requests.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static chemlab.security.user.Role.ROLE_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class DefaultUserRegistrationService implements UserRegistrationService {
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserService  userService;
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private CustomMapper customMapper;
    @Autowired
    private EmailService emailService;

    @Override
    public User register(RegisterUserRequest userDto) throws UsernameExistException, EmailExistException, UserNotFoundException {
        userValidator.validateNewUsernameAndEmail(EMPTY, userDto.getUsername(), userDto.getEmail());

        User user = customMapper.registerUserFromDto(userDto);

        userProfileService.persistUserWithDuplicateCheck(user);
        return user;
    }

    @Override
    public User addNewUser(CreateUserRequest createUserRequest) throws UsernameExistException, EmailExistException, IOException, UserNotFoundException {
        userValidator.validateNewUsernameAndEmail(EMPTY, createUserRequest.getUsername(), createUserRequest.getEmail());

        String password = userAuthenticationService.generatePassword();
        User user = customMapper.createUserFromDto(createUserRequest);
        user.setPassword(password);

        userProfileService.persistUserWithDuplicateCheck(user);
        return user;
    }

    @Override
    public User updateUser(UpdateUserRequest updateUserRequest) throws UsernameExistException, EmailExistException, UserNotFoundException {
        // Validate uniqueness using userId (single DB lookup inside validateEditUsernameAndEmail)
        User userToUpdate = userValidator.validateEditUsernameAndEmail(updateUserRequest.userId, updateUserRequest.username, updateUserRequest.email);
        customMapper.updateUserFromDto(updateUserRequest, userToUpdate);
        try {
            if (updateUserRequest.profileImg != null && !updateUserRequest.profileImg.isEmpty()) {
                userProfileService.saveProfileImg(userToUpdate, updateUserRequest.profileImg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userProfileService.persistUserWithDuplicateCheck(userToUpdate);
        return userToUpdate;
    }

    @Override
    public void deleteUser(String username) {
        userService.findUserByUsername(username).ifPresent(value -> {
            String[] imageUrlParts = value.getProfileImgUrl().split("/");
            String part = imageUrlParts[imageUrlParts.length - 2];
            if (!(part.equals("robohash"))) {
                String imageSlug = imageUrlParts[imageUrlParts.length - 1];
                userProfileService.deleteProfileImage(imageSlug);
            }
            userService.delete(value);
        });
    }
}
