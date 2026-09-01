package chemlab.model.shared;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateFlashcardRequest {
    @NotBlank final String userId;
    @NotBlank final String question;
    @NotBlank final String answer;
}