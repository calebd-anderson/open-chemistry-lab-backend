package auth.jwt;

import auth.user.RegisteredUserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
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

import static auth.config.SecurityConstants.*;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Arrays.stream;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    // generate the token
    public String generateJwtToken(RegisteredUserPrincipal userPrincipal, String issuer) {
        String[] claims = getClaimsFromUser(userPrincipal);
        String userRole = getRoleFromUser(userPrincipal);
        return JWT.create().withIssuer(issuer)
                .withAudience("open-chem-lab")
                .withIssuedAt(new Date()).withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withClaim("role", userRole)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .withExpiresAt(new Date(System.currentTimeMillis() + 300))
                .sign(HMAC512(secret.getBytes()));
    }

    // authorities from token
    public List<GrantedAuthority> getAuthorities(String token, String issuer) {
        String[] claims = getClaimsFromToken(token, issuer);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // get authentication of user
    // once token verified, set authentication in spring security context
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthToken = new
                UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthToken;
    }

    public boolean isTokenValid(String username, String token, String issuer) {
        JWTVerifier verifier = getJWTVerifier(issuer);
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    public String getSubject(String token, String issuer) {
        JWTVerifier verifier = getJWTVerifier(issuer);
        return verifier.verify(token).getSubject();
    }

    // single responsibility principle private methods
    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    // claims from token
    private String[] getClaimsFromToken(String token, String issuer) {
        JWTVerifier verifier = getJWTVerifier(issuer);
        List<String> claims = verifier.verify(token).getClaim(AUTHORITIES).asList(String.class);
        String role = verifier.verify(token).getClaim("role").asString();
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

    private JWTVerifier getJWTVerifier(String issuer) {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = HMAC512(secret);
//            verifier = JWT.require(algorithm).withIssuer(issuer).withAudience().build();
            verifier = JWT.require(algorithm).withIssuer(issuer).build();
        } catch (JWTVerificationException exception) {
            // hide actual exception
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }
}
