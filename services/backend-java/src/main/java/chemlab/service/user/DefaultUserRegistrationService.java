package chemlab.service.user;

import chemlab.config.CustomMapper;
import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.NotAnImageFileException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

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
    CustomMapper customMapper;
    @Autowired
    private EmailService emailService;

    @Override
    public User register(RegisterUserRequest userDto) throws UserNotFoundException, UsernameExistException, EmailExistException {
        userValidator.validateNewUsernameAndEmail(EMPTY, userDto.getUsername(), userDto.getEmail());
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
            userProfileService.persistUserWithDuplicateCheck(user);
            return user;
//        } catch (IOException | NotAnImageFileException e) {
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create user during registration.", e);
        }
    }

    @Override
    public User addNewUser(CreateUserRequest createUserRequest) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        userValidator.validateNewUsernameAndEmail(EMPTY, createUserRequest.getUsername(), createUserRequest.getEmail());

        User user = buildUserEntity(
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

    public User buildUserEntity(String firstName,
                                 String lastName,
                                 String username,
                                 String email,
                                 String password,
                                 String roleName,
                                 MultipartFile profileImg) throws IOException {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(userAuthenticationService.encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);

        Role resolvedRole = StringUtils.isNotBlank(roleName) ? userAuthenticationService.getRoleEnumName(roleName) : ROLE_USER;
        user.setRole(resolvedRole.name());
        user.setAuthorities(resolvedRole.getAuthorities());

        if (profileImg != null && !profileImg.isEmpty()) {
            userProfileService.saveProfileImg(user, profileImg);
        } else {
            user.setProfileImgUrl(userProfileService.getTemporaryProfileImageUrl(username));
        }

        return user;
    }

    @Override
    public void deleteUser(String username) {
        Optional<User> user = userService.findUserByUsername(username);
        user.ifPresent(value -> {
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
