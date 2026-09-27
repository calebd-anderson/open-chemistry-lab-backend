package chemlab.controller;

import chemlab.domain.exceptions.*;
import chemlab.infrastructure.pubchem.exceptions.PugApiException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler implements ErrorController {
    public static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    public static final String ERROR_PATH = "/error";
    private static final String DOCS_URL_PREFIX = "https://api.chemlab.com/errors/";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ProblemDetail> accountDisabledException(HttpServletRequest request) {
        return createProblemDetailResponseEntity(request, FORBIDDEN, "Your pre-existing account has been disabled. If this is an error, please contact administration.", null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> badCredentialsException(HttpServletRequest request) {
        return createProblemDetailResponseEntity(request, UNAUTHORIZED, INCORRECT_CREDENTIALS, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> accessDeniedException(HttpServletRequest request) {
        return createProblemDetailResponseEntity(request, FORBIDDEN, "You do not have enough permission.", null);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ProblemDetail> lockedException(HttpServletRequest request) {
        return createProblemDetailResponseEntity(request, FORBIDDEN, "Your account has been locked. Please contact administration", null);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ProblemDetail> tokenExpiredException(HttpServletRequest request, TokenExpiredException exception) {
        return createProblemDetailResponseEntity(request, UNAUTHORIZED, "Your session has expired. Please log in again.", exception);
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<ProblemDetail> emailExistException(HttpServletRequest request, EmailExistException exception) {
        return createProblemDetailResponseEntity(request, CONFLICT, null, exception);
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<ProblemDetail> usernameExistException(HttpServletRequest request, UsernameExistException exception) {
        return createProblemDetailResponseEntity(request, CONFLICT, null, exception);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ProblemDetail> emailNotFoundException(HttpServletRequest request, EmailNotFoundException exception) {
        return createProblemDetailResponseEntity(request, NOT_FOUND, null, exception);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> userNotFoundException(HttpServletRequest request, UserNotFoundException exception) {
        return createProblemDetailResponseEntity(request, NOT_FOUND, null, exception);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> methodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createProblemDetailResponseEntity(request, METHOD_NOT_ALLOWED, String.format("This request method is not allowed on this endpoint. Please send a '%s' request.", supportedMethod), exception);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> notFoundErrorException(HttpServletRequest request, Exception exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(request, NOT_FOUND, "The requested resource was not found.", exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> internalServerErrorException(HttpServletRequest request, Exception exception) {
        log.error("Unhandled exception: ", exception);
        return createProblemDetailResponseEntity(request, INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.", exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException ex) {
        return createProblemDetailResponseEntity(request, BAD_REQUEST, "Validation failed", ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> constraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
        return createProblemDetailResponseEntity(request, BAD_REQUEST, "Constraint violation", ex);
    }

    @ExceptionHandler(NoFlashcardsCreatedException.class)
    public ResponseEntity<ProblemDetail> noFlashcardsCreatedException(HttpServletRequest request, NoFlashcardsCreatedException ex) {
        return createProblemDetailResponseEntity(request, BAD_REQUEST, null, ex);
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public ResponseEntity<ProblemDetail> notAnImageFileException(HttpServletRequest request, NotAnImageFileException exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(request, BAD_REQUEST, null, exception);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ProblemDetail> notFoundException(HttpServletRequest request, NoResultException exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(request, NOT_FOUND, null, exception);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ProblemDetail> iOException(HttpServletRequest request, IOException exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(request, INTERNAL_SERVER_ERROR, "Error occurred while processing file.", exception);
    }

    @ExceptionHandler(PugApiException.class)
    public ResponseEntity<ProblemDetail> pugApiException(HttpServletRequest request, PugApiException exception) {
        log.error(exception.getMessage());
        return createProblemDetailResponseEntity(request, NOT_FOUND, null, exception);
    }

    private ResponseEntity<ProblemDetail> createProblemDetailResponseEntity(HttpServletRequest request, HttpStatus httpStatus, String detail, Exception ex) {
        return createProblemDto(request, httpStatus, detail, ex);
    }

    private ResponseEntity<ProblemDetail> createProblemDto(HttpServletRequest request, HttpStatus httpStatus, String detail, Exception ex) {
        String finalDetail = (detail != null) ? detail : (ex != null ? ex.getMessage() : httpStatus.getReasonPhrase());

        // Prevent leaking implementation details in the 'detail' field for sensitive errors
        if (isSensitiveException(ex) && (finalDetail == null || isSensitiveMessage(finalDetail))) {
            finalDetail = "An error occurred while processing your request.";
        }

        ProblemDetail problemDetail = createProblemDetail(request, httpStatus, finalDetail, ex);

        if (ex != null) {
            enrichProblemDetail(problemDetail, ex);
        }

        return ResponseEntity.status(httpStatus).body(problemDetail);
    }

    private boolean isSensitiveException(Exception ex) {
        return (ex instanceof AuthenticationException ||
                ex instanceof AccessDeniedException ||
                ex instanceof java.sql.SQLException ||
                ex instanceof IOException);
    }

    private boolean isSensitiveMessage(String message) {
        return message.contains("SecurityContext") || message.contains("SQL") || message.contains("Exception");
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

    private ProblemDetail createProblemDetail(HttpServletRequest request, HttpStatus httpStatus, String detail, Exception ex) {
        String type = DOCS_URL_PREFIX + httpStatus.name().toLowerCase();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, detail);
        problemDetail.setType(java.net.URI.create(type));
        problemDetail.setTitle(httpStatus.getReasonPhrase());
        problemDetail.setProperty("instance", request.getRequestURI());
        problemDetail.setProperty("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        return problemDetail;
    }
}
