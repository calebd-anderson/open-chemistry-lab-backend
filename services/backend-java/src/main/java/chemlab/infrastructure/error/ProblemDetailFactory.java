package chemlab.infrastructure.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class ProblemDetailFactory {

    private static final String DOCS_URL_PREFIX = "https://api.chemlab.com/errors/";

    /**
     * Creates a ProblemDetail for a web request.
     */
    public ProblemDetail createProblemDetail(HttpServletRequest request, HttpStatus status, String detail, String title, Exception ex) {
        String finalDetail = sanitizeDetail(status, detail, ex);
        String type = DOCS_URL_PREFIX + status.name().toLowerCase();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, finalDetail);
        problemDetail.setType(URI.create(type));
        problemDetail.setTitle(title != null ? title : status.getReasonPhrase());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));

        if (ex != null) {
            enrichProblemDetail(problemDetail, ex);
        }

        return problemDetail;
    }

    /**
     * Creates a ProblemDetail when HttpServletRequest is not available (e.g., in some filter contexts).
     */
    public ProblemDetail createProblemDetail(HttpStatus status, String detail, String title, Exception ex) {
        String finalDetail = sanitizeDetail(status, detail, ex);
        String type = DOCS_URL_PREFIX + status.name().toLowerCase();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, finalDetail);
        problemDetail.setType(URI.create(type));
        problemDetail.setTitle(title != null ? title : status.getReasonPhrase());
        problemDetail.setProperty("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));

        if (ex != null) {
            enrichProblemDetail(problemDetail, ex);
        }

        return problemDetail;
    }

    private String sanitizeDetail(HttpStatus status, String detail, Exception ex) {
        String finalDetail = (detail != null) ? detail : (ex != null ? ex.getMessage() : status.getReasonPhrase());

        if (isSensitiveException(ex) && (finalDetail == null || isSensitiveMessage(finalDetail))) {
            return "An error occurred while processing your request.";
        }
        return finalDetail;
    }

    private boolean isSensitiveException(Exception ex) {
        return (ex instanceof AuthenticationException ||
                ex instanceof org.springframework.security.access.AccessDeniedException ||
                ex instanceof java.sql.SQLException ||
                ex instanceof java.io.IOException);
    }

    private boolean isSensitiveMessage(String message) {
        return message.contains("SecurityContext") || message.contains("SQL") || message.contains("Exception");
    }

    private void enrichProblemDetail(ProblemDetail problemDetail, Exception ex) {
        // We don't have access to the request here to add 'instance', but we can add other metadata if needed.
        // For now, keeping it simple as the request-based method handles the heavy lifting.
    }
}
