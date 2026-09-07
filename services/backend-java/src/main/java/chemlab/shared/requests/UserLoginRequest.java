package chemlab.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotEmpty(message = "The username is required.")
    private String username;
    @NotEmpty(message = "The password is required.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
