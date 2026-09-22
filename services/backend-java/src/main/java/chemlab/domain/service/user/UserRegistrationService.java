package chemlab.domain.service.user;

import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.shared.requests.RegisterUserRequest;

public interface UserRegistrationService {
    User register(RegisterUserRequest registerUserRequest) throws UserNotFoundException, UsernameExistException, EmailExistException;
}
