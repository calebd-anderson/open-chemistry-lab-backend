package shared;

public class FlashcardDto {

	private String userId;
	private String question;
	private String answer;

	public FlashcardDto() {}

	public FlashcardDto(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}
	
	public FlashcardDto(String userId, String question, String answer) {
		this.userId = userId;
		this.question = question;
		this.answer = answer;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}
}