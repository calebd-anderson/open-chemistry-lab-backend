package chemlab.model.shared;

import jakarta.validation.constraints.NotBlank;

public record CreateFlashcardRequest(@NotBlank String userId, @NotBlank String question, @NotBlank String answer) {
}