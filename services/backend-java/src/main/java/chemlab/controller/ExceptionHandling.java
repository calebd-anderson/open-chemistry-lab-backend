package chemlab.controller;

import chemlab.domain.exceptions.*;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ExceptionHandling implements ErrorController {
    public static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    public static final String ERROR_PATH = "/error";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ProblemDetail> accountDisabledException() {
        return createProblemDetailResponseEntity(FORBIDDEN, "Your pre-existing account has been disabled. If this is an error, please contact administration.", null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> badCredentialsException() {
        return createProblemDetailResponseEntity(UNAUTHORIZED, INCORRECT_CREDENTIALS, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> accessDeniedException() {
        return createProblemDetailResponseEntity(FORBIDDEN, "You do not have enough permission.", null);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ProblemDetail> lockedException() {
        return createProblemDetailResponseEntity(FORBIDDEN, "Your account has been locked. Please contact administration", null);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ProblemDetail> tokenExpiredException(TokenExpiredException exception) {
        return createProblemDetailResponseEntity(UNAUTHORIZED, null, exception);
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<ProblemDetail> emailExistException(EmailExistException exception) {
        return createProblemDetailResponseEntity(CONFLICT, null, exception);
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<ProblemDetail> usernameExistException(UsernameExistException exception) {
        return createProblemDetailResponseEntity(CONFLICT, null, exception);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ProblemDetail> emailNotFoundException(EmailNotFoundException exception) {
        return createProblemDetailResponseEntity(NOT_FOUND, null, exception);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> userNotFoundException(UserNotFoundException exception) {
        return createProblemDetailResponseEntity(NOT_FOUND, null, exception);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createProblemDetailResponseEntity(METHOD_NOT_ALLOWED, String.format("This request method is not allowed on this endpoint. Please send a '%s' request.", supportedMethod), exception);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> notFoundErrorException(Exception exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(NOT_FOUND, "The requested resource was not found.", exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> internalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(INTERNAL_SERVER_ERROR, "An error occurred while processing the request", exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return createProblemDetailResponseEntity(BAD_REQUEST, "Validation failed", ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return createProblemDetailResponseEntity(BAD_REQUEST, "Constraint violation", ex);
    }

    @ExceptionHandler(NoFlashcardsCreatedException.class)
    public ResponseEntity<ProblemDetail> noFlashcardsCreatedException(NoFlashcardsCreatedException ex) {
        return createProblemDetailResponseEntity(BAD_REQUEST, null, ex);
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public ResponseEntity<ProblemDetail> notAnImageFileException(NotAnImageFileException exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(BAD_REQUEST, null, exception);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ProblemDetail> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(NOT_FOUND, null, exception);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ProblemDetail> iOException(IOException exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(INTERNAL_SERVER_ERROR, "Error occurred while processing file.", exception);
    }

    @ExceptionHandler(PugApiException.class)
    public ResponseEntity<ProblemDetail> pugApiException(PugApiException exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(NOT_FOUND, null, exception);
    }

    private ResponseEntity<ProblemDetail> createProblemDetailResponseEntity(HttpStatus httpStatus, String detail, Exception ex) {
        String finalDetail = (detail != null) ? detail : (ex != null ? ex.getMessage() : httpStatus.getReasonPhrase());
        ProblemDetail problemDetail = createProblemDetail(httpStatus, finalDetail);

        if (ex != null) {
            enrichProblemDetail(problemDetail, ex);
        }

        return ResponseEntity.status(httpStatus).body(problemDetail);
    }

    private void enrichProblemDetail(ProblemDetail problemDetail, Exception ex) {
        if (ex instanceof MethodArgumentNotValidException mev) {
            List<String> errors = mev.getBindingResult().getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            problemDetail.setProperty("errors", errors);
        } else if (ex instanceof ConstraintViolationException cve) {
            List<String> errors = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
            problemDetail.setProperty("errors", errors);
        }
    }

    private ProblemDetail createProblemDetail(HttpStatus httpStatus, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, detail);
        problemDetail.setTitle(httpStatus.getReasonPhrase());
        return problemDetail;
    }
}