package chemlab.service.user;

import chemlab.domain.model.user.User;
import chemlab.domain.repository.UserRepository;
import chemlab.domain.service.user.UserAuthenticationService;
import chemlab.security.user.LoginAttemptService;
import chemlab.security.user.RegisteredUserPrincipal;
import chemlab.security.user.Role;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static chemlab.service.user.config.UserImplementationConstant.NO_USER_FOUND_BY_USERNAME;

@Service
@Log4j2
public class DefaultUserAuthenticationService implements UserAuthenticationService, UserDetailsService {
    private final UserRepository userRepo;
    private final LoginAttemptService loginAttemptService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    DefaultUserAuthenticationService(UserRepository userRepo, LoginAttemptService loginAttemptService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.loginAttemptService = loginAttemptService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // update 500 instead of user not found ?
    public void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public void resetPassword(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepo.save(user);
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

    public String generatePassword() {
        return RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
