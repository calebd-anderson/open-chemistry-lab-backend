package chemlab.auth.jwt;

import chemlab.auth.user.RegisteredUserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static chemlab.auth.config.SecurityConstants.*;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Arrays.stream;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;
    private final String audience = "open-chem-lab";

    // generate the token
    public String generateJwtToken(RegisteredUserPrincipal userPrincipal, String issuer) {
        String[] claims = getClaimsFromUser(userPrincipal);
        String userRole = getRoleFromUser(userPrincipal);
        return JWT.create().withIssuer(issuer)
                .withAudience(audience)
                .withIssuedAt(new Date()).withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withClaim("role", userRole)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));
    }

    // get authentication of user
    // once token verified, set authentication in spring security context
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthToken = new
                UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthToken;
    }

    public DecodedJWT verifyToken(String token, String issuer) {
        Algorithm algorithm = HMAC512(secret);
        // audience is the intended recipient of the token, in this case, our application
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).withAudience(audience).build();
        return verifier.verify(token);
    }

    // authorities from token
    public List<GrantedAuthority> getAuthorities(DecodedJWT token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // claims from token
    private String[] getClaimsFromToken(DecodedJWT token) {
        List<String> claims = token.getClaim(AUTHORITIES).asList(String.class);
        String role = token.getClaim("role").asString();
        claims.add(role);
        return claims.toArray(new String[0]);
    }

    // claims from user
    private String[] getClaimsFromUser(RegisteredUserPrincipal userPrincipal) {
        // list of string
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]); // return as String array
    }

    // role from user
    private String getRoleFromUser(RegisteredUserPrincipal userPrincipal) {
        return userPrincipal.getRole();
    }
}
