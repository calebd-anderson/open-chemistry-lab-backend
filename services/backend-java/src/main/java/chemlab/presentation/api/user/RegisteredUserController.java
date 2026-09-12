package chemlab.presentation.api.user;

import chemlab.domain.service.user.RegisteredUserService;
import chemlab.presentation.ExceptionHandling;
import chemlab.domain.exceptions.EmailExistException;
import chemlab.domain.exceptions.NotAnImageFileException;
import chemlab.domain.exceptions.UserNotFoundException;
import chemlab.domain.exceptions.UsernameExistException;
import chemlab.infrastructure.robohash.RoboHashService;
import chemlab.domain.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/user")
public class RegisteredUserController extends ExceptionHandling {

    private final RegisteredUserService userService;
    private final RoboHashService roboHashService;

    public RegisteredUserController(RegisteredUserService userService, RoboHashService roboHashService) {
        this.userService = userService;
        this.roboHashService = roboHashService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
//		return ResponseEntity.ok().body(userService
//				.getUsers()
//				.stream()
//				.map(mapper::toDao)
//				.collect(Collectors.toList()));
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
        if (profileImg != null) {
            validateMultipartFile("profileImg", profileImg);
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE, "image/webp").contains(profileImg.getContentType())) {
                throw new NotAnImageFileException("Invalid content type for profile image.");
            }
        }
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
        if (profileImg != null) {
            validateMultipartFile("profileImg", profileImg);
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE, "image/webp").contains(profileImg.getContentType())) {
                throw new NotAnImageFileException("Invalid content type for profile image.");
            }
        }
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
        if (profileImg != null) {
            validateMultipartFile("profileImg", profileImg);
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE, "image/webp").contains(profileImg.getContentType())) {
                throw new NotAnImageFileException("Invalid content type for profile image.");
            }
        }
        User updatedUser = userService.editUser(userId, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImg);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/updateprofileimg")
    public ResponseEntity<User> update(@RequestParam("username") String username,
                                       @RequestParam(value = "profileImg") MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        validateMultipartFile("profileImg", profileImg);
        // Check file size (max 5MB)
        if (profileImg.getSize() > 5 * 1024 * 1024) {
            throw new NotAnImageFileException("Profile image is too large. Maximum size is 5MB.");
        }
        // Validate content type is a real image (not just saying it's a jpg but being a shell script)
        if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE, "image/webp").contains(profileImg.getContentType())) {
            throw new NotAnImageFileException("Invalid content type for profile image.");
        }
        User user = userService.updateProfileImage(username, profileImg);
        return new ResponseEntity<>(user, OK);
    }

    private void validateMultipartFile(String parameterName, MultipartFile file) throws NotAnImageFileException {
        if (file == null || file.isEmpty()) {
            return; // Let caller handle empty case
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!extension.matches("(jpg|jpeg|png|gif|webp)$")) {
            throw new NotAnImageFileException(parameterName + " has an invalid file type.");
        }
    }

    private String getFileExtension(String filename) {
        if (filename != null) {
            int lastDot = filename.lastIndexOf('.');
            if (lastDot > 0 && lastDot < filename.length() - 1) {
                return filename.substring(lastDot + 1).toLowerCase();
            }
        }
        return "";
    }

    // validate
    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        Optional<User> user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user.get(), OK);
    }

    @GetMapping(path = "/image/{userId}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("userId") String userId, @PathVariable("fileName") String fileName) throws IOException {
        return userService.getProfileImage(userId, fileName);
    }

    @GetMapping(path = "/image/robohash/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        return roboHashService.getProfileImage(username);
    }
}
