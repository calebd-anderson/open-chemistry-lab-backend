package chemlab.domain.service.user;

import chemlab.domain.exceptions.EmailNotFoundException;
import chemlab.domain.model.user.User;

public interface UserAuthenticationService {
    void validateLoginAttempt(User user);
    void resetPassword(String email);
}
