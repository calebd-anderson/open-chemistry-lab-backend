package chemlab.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoFlashcardsCreatedException extends RuntimeException {
    public NoFlashcardsCreatedException(String message) {
        super(message);
    }
}
