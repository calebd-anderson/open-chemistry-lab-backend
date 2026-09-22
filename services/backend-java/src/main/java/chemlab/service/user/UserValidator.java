package chemlab.service.user;

import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.domain.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static chemlab.service.user.config.UserImplementationConstant.*;

@Component
public class UserValidator {
    @Autowired
    UserRepository userRepository;

    public User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        // Only look up by newUsername/newEmail if they are provided to avoid unnecessary DB calls and NPEs
        Optional<User> userByNewUsername = StringUtils.isNotBlank(newUsername) ? userRepository.findByUsername(newUsername) : Optional.empty();
        Optional<User> userByNewEmail = StringUtils.isNotBlank(newEmail) ? userRepository.findByEmail(newEmail) : Optional.empty();

        if (StringUtils.isNotBlank(currentUsername)) {
            // Update scenario: ensure the current user exists and any found user for the new
            // username/email is either null or the same as the current user
            Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
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

    public User validateEditUsernameAndEmail(String userId, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        // Resolve the current user once (by id) and then perform uniqueness checks against
        // the new username/email — this avoids an extra lookup of the current user by username.
        User currentUser = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("No user found by id: " + userId));

        Optional<User> userByNewUsername = StringUtils.isNotBlank(newUsername) ? userRepository.findByUsername(newUsername) : Optional.empty();
        Optional<User> userByNewEmail = StringUtils.isNotBlank(newEmail) ? userRepository.findByEmail(newEmail) : Optional.empty();

        if (userByNewUsername.isPresent() && !currentUser.getId().equals(userByNewUsername.get().getId())) {
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        if (userByNewEmail.isPresent() && !currentUser.getId().equals(userByNewEmail.get().getId())) {
            throw new EmailExistException(EMAIL_ALREADY_EXISTS);
        }
        return currentUser;
    }
}
