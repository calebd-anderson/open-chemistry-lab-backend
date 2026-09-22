package chemlab.controller.api.auth;

import chemlab.controller.ExceptionHandling;
import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.EmailNotFoundException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.domain.model.user.User;
import chemlab.domain.service.user.UserService;
import chemlab.security.http.HttpResponse;
import chemlab.security.jwt.JwtTokenProvider;
import chemlab.security.user.RegisteredUserPrincipal;
import chemlab.shared.requests.RegisterUserRequest;
import chemlab.shared.requests.UserLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;

import static chemlab.security.config.SecurityConstants.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.CREATED;

@Log4j2
@RestController
@RequestMapping("/auth")
public class AuthController extends ExceptionHandling {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody UserLoginRequest user, HttpServletRequest req) {
        Authentication auth = authenticate(user.getUsername(), user.getPassword());
        if (auth.isAuthenticated()) {
            userService.saveLastLogin(new Date(), user.getUsername());
            User loginUser = userService.findUserByUsername(user.getUsername()).orElseThrow();
            RegisteredUserPrincipal userPrincipal = new RegisteredUserPrincipal(loginUser);
            String issuer = ServletUriComponentsBuilder.fromRequestUri(req)
                    .replacePath(null)
                    .build()
                    .toUriString();
            HttpHeaders jwtHeader = getJwtHeader(userPrincipal, issuer);
            return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserRequest user) throws UserNotFoundException, UsernameExistException, EmailExistException {
        // might want validation
        User newUser = userService.register(user);
        return new ResponseEntity<>(newUser, CREATED);
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        throw new NotImplementedException();
//        userService.resetPassword(email);
//        log.info("Email with new password sent to: {}", email);
//        return ResponseEntity.ok().build();
    }

    private Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private HttpHeaders getJwtHeader(RegisteredUserPrincipal userPrincipal, String issuer) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal, issuer));
        return headers;
    }
}
