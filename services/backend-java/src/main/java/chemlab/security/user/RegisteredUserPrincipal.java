package chemlab.security.user;

import chemlab.domain.model.user.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

// implement UserDetails to be returned by UserDetailsService
public class RegisteredUserPrincipal implements UserDetails {
    
	private final User user;	// passing our user to spring security

    public RegisteredUserPrincipal(User user) {
        this.user = user;
    }

	@Override
	@NullMarked
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return stream(this.user.getAuthorities()).map(SimpleGrantedAuthority::new).collect(toList());
	}

	public String getRole() {
		return user.getRole();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	@NullMarked
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	@NullMarked
	public boolean isAccountNonExpired() { return true;	}

	@Override
	public boolean isAccountNonLocked() { return this.user.isNotLocked(); }

	@Override
	@NullMarked
	public boolean isCredentialsNonExpired() { return true; }

	@Override
	public boolean isEnabled() { return this.user.isActive(); }
}
