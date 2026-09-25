package chemlab.shared.responses;

import chemlab.domain.model.game.Flashcard;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserResponseDTO {
    private String userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String profileImgUrl;
    private Date lastLoginDate;
    private Date joinDate;
    private String role;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;
    // game information
    private int highScore;
    private List<Flashcard> userFlashcards;
}
