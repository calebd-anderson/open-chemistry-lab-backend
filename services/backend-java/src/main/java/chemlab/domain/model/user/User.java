package chemlab.domain.model.user;

import chemlab.domain.model.game.Flashcard;
import chemlab.shared.requests.UserLoginRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "users")
@Data
@RequiredArgsConstructor
public class User {
    @Id
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String id;
    @Indexed(unique = true)
    private String userId;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String username;
    private String firstName;
    private String lastName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String profileImgUrl;
    private Date lastLoginDate;
    private Date joinDate;
    private String role;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;
    // game information
    private int highScore;
    private List<Flashcard> userFlashcards = new ArrayList<>();

    public User(UserLoginRequest userDTO) {
        this.password = userDTO.getPassword();
        this.username = userDTO.getUsername();
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
