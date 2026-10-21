package chemlab.security.jwt;

public record JwtUserPrincipal(String username, String userId) implements java.security.Principal {
    @Override
    public String getName() {
        return username;
    }
}
