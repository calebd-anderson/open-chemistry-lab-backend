package chemlab.service.user;

import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.domain.service.user.UserAuthenticationService;
import chemlab.domain.service.user.UserProfileService;
import chemlab.domain.service.user.UserRegistrationService;
import chemlab.security.user.Role;
import chemlab.shared.requests.RegisterUserRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

import static chemlab.security.user.Role.ROLE_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class DefaultUserRegistrationService implements UserRegistrationService {
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Autowired
    private UserValidator userValidator;

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
}
