package chemlab.presentation.api.auth;

import chemlab.auth.http.HttpResponse;
import chemlab.auth.jwt.JwtTokenProvider;
import chemlab.auth.user.RegisteredUserPrincipal;
import chemlab.domain.service.user.RegisteredUserService;
import chemlab.presentation.ExceptionHandling;
import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.EmailNotFoundException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.shared.requests.UserLoginRequest;
import chemlab.shared.requests.UserRegisterRequest;
import chemlab.domain.model.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;

import static chemlab.auth.config.SecurityConstants.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/auth")
public class AuthController extends ExceptionHandling {

    public static final String EMAIL_SENT = "Email with new password sent to: ";
    private final RegisteredUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(RegisteredUserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody UserLoginRequest user, HttpServletRequest req) {
        Authentication auth = authenticate(user.getUsername(), user.getPassword());
        if (auth.isAuthenticated()) {
            userService.saveLastLogin(new Date(), user.getUsername());
            User loginUser = userService.findUserByUsername(user.getUsername());
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
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterRequest user) throws UserNotFoundException, UsernameExistException, EmailExistException {
        // might want validation
        User newUser = userService.register(user);
        return new ResponseEntity<>(newUser, CREATED);
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase()), httpStatus);
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
