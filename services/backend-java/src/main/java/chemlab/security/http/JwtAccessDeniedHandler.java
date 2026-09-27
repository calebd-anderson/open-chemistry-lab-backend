package chemlab.security.http;

import chemlab.infrastructure.error.ProblemDetailFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private ProblemDetailFactory problemDetailFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(@NonNull HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.debug("JWT Access denied handler triggered");
        // Use the factory to create consistent error responses
        var problemDetail = problemDetailFactory.createProblemDetail(request, HttpStatus.FORBIDDEN, "Access denied", "Forbidden", accessDeniedException);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // Write using the factory-provided ProblemDetail as JSON
        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }
}
