package shared;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FlashcardDto {

    private String userId;
    private String question;
    private String answer;

    public FlashcardDto(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}