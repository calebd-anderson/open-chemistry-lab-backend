package presentation.controllers.api.user;

import auth.http.HttpResponse;
import auth.jwt.JwtTokenProvider;
import auth.user.RegisteredUserPrincipal;
import chemlab.domain.user.RegisteredUserService;
import chemlab.exceptions.ExceptionHandling;
import chemlab.exceptions.domain.*;
import chemlab.model.user.User;
import infrastructure.robohash.RoboHashService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shared.UserLoginDto;
import shared.UserRegisterDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static auth.config.SecurityConstants.JWT_TOKEN_HEADER;
import static chemlab.service.user.config.FileConstants.FORWARD_SLASH;
import static chemlab.service.user.config.FileConstants.USER_FOLDER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/user")
public class RegisteredUserController extends ExceptionHandling {

    public static final String EMAIL_SENT = "Email with new password sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully.";
    private final RegisteredUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private final RoboHashService roboHashService;

    @Autowired
    public RegisteredUserController(RegisteredUserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, RoboHashService roboHashService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.roboHashService = roboHashService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody UserLoginDto user, HttpServletRequest req) {
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
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterDto user) throws UserNotFoundException, UsernameExistException, EmailExistException {
        // might want validation
        User newUser = userService.register(user);
        return new ResponseEntity<>(newUser, CREATED);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("isActive") String isActive,          // boolean
                                           @RequestParam("isNonLocked") String isNonLocked,    // boolean
                                           @RequestParam("role") String role,
                                           @RequestParam(value = "profileImg", required = false) MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User newUser = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImg);
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
                                       @RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("role") String role,
                                       @RequestParam("isActive") String isActive,            // boolean
                                       @RequestParam("isNonLocked") String isNonLocked,    // boolean
                                       @RequestParam(value = "profileImg", required = false) MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImg);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @PostMapping("/edit")
    public ResponseEntity<User> edit(@RequestParam("userId") String userId,
                                     @RequestParam("firstName") String firstName,
                                     @RequestParam("lastName") String lastName,
                                     @RequestParam("username") String username,
                                     @RequestParam("email") String email,
                                     @RequestParam("role") String role,
                                     @RequestParam("isActive") String isActive,               // boolean
                                     @RequestParam("isNonLocked") String isNonLocked,         // boolean
                                     @RequestParam(value = "profileImg", required = false) MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User updatedUser = userService.editUser(userId, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImg);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @PostMapping("/updateprofileimg")
    public ResponseEntity<User> update(@RequestParam("username") String username, @RequestParam(value = "profileImg") MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User user = userService.updateProfileImage(username, profileImg);
        return new ResponseEntity<>(user, OK);
    }

    // validate
    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
//		return ResponseEntity.ok().body(userService
//				.getUsers()
//				.stream()
//				.map(mapper::toDao)
//				.collect(Collectors.toList()));
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        return roboHashService.getProfileImage(username);
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

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}
