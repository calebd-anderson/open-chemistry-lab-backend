package shared;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FlashcardDto {
    private final String userId;
    private final String question;
    private final String answer;
}