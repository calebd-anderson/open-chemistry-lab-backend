package chemlab.domain.repository;

import chemlab.domain.model.user.User;

import java.util.List;
import java.util.Optional;

public interface RegisteredUserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(String userId);

    void deleteById(String userId);

    List<User> findAll();
}
