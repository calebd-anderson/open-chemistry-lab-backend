package chemlab.controller;

import chemlab.domain.exceptions.*;
import chemlab.infrastructure.error.ProblemDetailFactory;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler implements ErrorController {

    @Autowired
    private ProblemDetailFactory problemDetailFactory;

    public static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    public static final String ERROR_PATH = "/error";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ProblemDetail> accountDisabledException(HttpServletRequest request) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, FORBIDDEN, "Your pre-existing account has been disabled. If this is an error, please contact administration.", "Forbidden", null);
        return ResponseEntity.status(FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> badCredentialsException(HttpServletRequest request) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, UNAUTHORIZED, INCORRECT_CREDENTIALS, "Unauthorized", null);
        return ResponseEntity.status(UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> accessDeniedException(HttpServletRequest request) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, FORBIDDEN, "You do not have enough permission.", "Forbidden", null);
        return ResponseEntity.status(FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ProblemDetail> lockedException(HttpServletRequest request) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, FORBIDDEN, "Your account has been locked. Please contact administration", "Forbidden", null);
        return ResponseEntity.status(FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ProblemDetail> tokenExpiredException(HttpServletRequest request, TokenExpiredException exception) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, UNAUTHORIZED, "Your session has expired. Please log in again.", "Unauthorized", exception);
        return ResponseEntity.status(UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<ProblemDetail> emailExistException(HttpServletRequest request, EmailExistException exception) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, CONFLICT, null, "Conflict", exception);
        return ResponseEntity.status(CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<ProblemDetail> usernameExistException(HttpServletRequest request, UsernameExistException exception) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, CONFLICT, null, "Conflict", exception);
        return ResponseEntity.status(CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ProblemDetail> emailNotFoundException(HttpServletRequest request, EmailNotFoundException exception) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, NOT_FOUND, null, "Not Found", exception);
        return ResponseEntity.status(NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> userNotFoundException(HttpServletRequest request, UserNotFoundException exception) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, NOT_FOUND, null, "Not Found", exception);
        return ResponseEntity.status(NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> methodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        String message = String.format("This request method is not allowed on this endpoint. Please send a '%s' request.", supportedMethod);
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, METHOD_NOT_ALLOWED, message, "Method Not Allowed", exception);
        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(problemDetail);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> notFoundErrorException(HttpServletRequest request, Exception exception) {
        log.error(exception.getMessage());
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, NOT_FOUND, "The requested resource was not found.", "Not Found", exception);
        return ResponseEntity.status(NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> internalServerErrorException(HttpServletRequest request, Exception exception) {
        log.error("Unhandled exception: ", exception);
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.", "Internal Server Error", exception);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, BAD_REQUEST, "Validation failed", "Bad Request", ex);
        return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> constraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, BAD_REQUEST, "Constraint violation", "Bad Request", ex);
        return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(NoFlashcardsCreatedException.class)
    public ResponseEntity<ProblemDetail> noFlashcardsCreatedException(HttpServletRequest request, NoFlashcardsCreatedException ex) {
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, BAD_REQUEST, null, "Bad Request", ex);
        return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public ResponseEntity<ProblemDetail> notAnImageFileException(HttpServletRequest request, NotAnImageFileException exception) {
        log.error(exception.getMessage());
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, BAD_REQUEST, null, "Bad Request", exception);
        return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ProblemDetail> notFoundException(HttpServletRequest request, NoResultException exception) {
        log.error(exception.getMessage());
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, NOT_FOUND, null, "Not Found", exception);
        return ResponseEntity.status(NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ProblemDetail> iOException(HttpServletRequest request, IOException exception) {
        log.error(exception.getMessage());
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, INTERNAL_SERVER_ERROR, "Error occurred while processing file.", "Internal Server Error", exception);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(PugApiException.class)
    public ResponseEntity<ProblemDetail> pugApiException(HttpServletRequest request, PugApiException exception) {
        log.error(exception.getMessage());
        ProblemDetail problemDetail = problemDetailFactory.createProblemDetail(request, NOT_FOUND, null, "Not Found", exception);
        return ResponseEntity.status(NOT_FOUND).body(problemDetail);
    }
}
