package chemlab.domain.service.user;

import chemlab.domain.exceptions.EmailNotFoundException;
import chemlab.domain.model.user.User;
import chemlab.security.user.Role;

public interface UserAuthenticationService {
    void validateLoginAttempt(User user);
    void resetPassword(String email);
    Role getRoleEnumName(String role);
    String encodePassword(String password);
    String generatePassword();
}
