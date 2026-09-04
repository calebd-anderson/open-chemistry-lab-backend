package chemlab.auth.http;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Setter
@Getter
public class HttpResponse
{
    private int httpStatusCode; // 200, 201 (ok), 400 (client error) 500 (server error)
    private HttpStatus httpStatus;
    private String reason;
    private String message;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "America/Chicago")
	private Date timeStamp;
    
    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message)
    {
		this.timeStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }

}
