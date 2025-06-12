package chemlab.domain.model.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserLoginDto {
    @NotEmpty(message = "The username is required.")
    private String username;
    @NotEmpty(message = "The password is required.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
