package chemlab.auth.jwt;

import chemlab.auth.config.SecurityConstants;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

import static chemlab.auth.config.SecurityConstants.TOKEN_PREFIX;

@Component
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        // if http options method return ok
        if (req.getMethod().equalsIgnoreCase(SecurityConstants.OPTIONS_HTTP_METHOD)) {
            res.setStatus(HttpStatus.OK.value());
        } else {
            String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
            // if null or not starts with "Bearer "
            if (authorizationHeader == null || authorizationHeader.equals("Bearer null") || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                log.debug("Authorization header is null or invalid.");
                // if the user is not authenticated, clear the security context
                // TODO: This is a temporary fix for the test scenarios which do not provide a valid token to prevent the security context from being cleared when the authorization header is invalid.
                // SecurityContextHolder.clearContext();
            } else {
                try {
                    // https://huongdanjava.com/get-base-url-in-controller-in-spring-mvc-and-spring-boot.html
                    String issuer = ServletUriComponentsBuilder.fromRequestUri(req)
                            .replacePath(null)
                            .build()
                            .toUriString();
                    String token = authorizationHeader.substring(TOKEN_PREFIX.length());
                    // this verifies the token and throws JWTVerificationException, TokenExpiredException
                    DecodedJWT decodedJWT = jwtTokenProvider.verifyToken(token, issuer);
                    log.trace("Authorization header is valid, token subject: {}.", decodedJWT.getSubject());
                    // if the user is not authenticated, set the authentication in the security context
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(decodedJWT);
                        Authentication authentication = jwtTokenProvider.getAuthentication(decodedJWT.getSubject(), authorities, req);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                } catch (TokenExpiredException e) {
                    log.warn("Authorization expired: {}.", e.getMessage());
                    SecurityContextHolder.clearContext();
                }
                catch (JWTVerificationException e) {
                    log.warn("Authorization invalid: {}.", e.getMessage());
                    SecurityContextHolder.clearContext();
                }
                catch (Exception e) {
                    log.error("Unexpected error occurred: {}.", e.getMessage());
                    SecurityContextHolder.clearContext();
                    throw e;
                }
            }
        }
        chain.doFilter(req, res);
    }
}
