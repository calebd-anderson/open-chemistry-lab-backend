package chemlab.shared.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UpdateUserRequest {
    public String userId;
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String role;
    public boolean isActive;    // boolean?
    public boolean isNotLocked; // boolean?
    public MultipartFile profileImg;
}
