package chemlab.shared.requests;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    @Email
    private String email;
    private boolean isActive;
    private boolean isNotLocked;
    private String role;
    MultipartFile profileImg;
}
