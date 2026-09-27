package chemlab.security.http;

import chemlab.infrastructure.error.ProblemDetailFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ProblemDetailFactory problemDetailFactory;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull AuthenticationException exception) throws IOException {
        log.debug("JWT Authentication entry point triggered");
        // Use the factory to create consistent error responses
        var problemDetail = problemDetailFactory.createProblemDetail(request, HttpStatus.UNAUTHORIZED, "Authentication failed", "Unauthorized", exception);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.addHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"");

        // Write using the factory-provided ProblemDetail directly
        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }
}
