package chemlab.domain.service.user;

import chemlab.domain.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getUsers();
    Optional<User> findUserByUsername(String username);
    void save(User user);
    void delete(User user);
}
