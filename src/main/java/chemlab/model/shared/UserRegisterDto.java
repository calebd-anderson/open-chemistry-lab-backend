package chemlab.model.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegisterDto {
    @Email(message = "The email address is invalid.", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String email;
    @NotEmpty(message = "The username is required.")
    private String username;
    @NotEmpty(message = "The first name is required.")
    private String firstName;
    @NotEmpty(message = "The last name is required.")
    private String lastName;
    @NotEmpty(message = "The password is required.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
