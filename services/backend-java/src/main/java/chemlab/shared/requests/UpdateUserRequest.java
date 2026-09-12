package chemlab.shared.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserRequest {
    public String userId;
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String role;
    public boolean isActive;    // boolean?
    public boolean isNonLocked; // boolean?
    public MultipartFile profileImg;

    UpdateUserRequest(String userId, String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImg) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.role = role;
        this.isNonLocked = isNonLocked;
//        this.isActive = Boolean.parseBoolean(isActive);
        this.isActive = isActive;
        this.profileImg = profileImg;
    }
}
