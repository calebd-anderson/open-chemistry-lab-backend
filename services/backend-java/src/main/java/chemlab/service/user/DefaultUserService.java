package chemlab.service.user;

import chemlab.domain.model.user.User;
import chemlab.domain.repository.UserRepository;
import chemlab.domain.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DefaultUserService implements UserService {

    @Autowired
    private UserRepository userRepo;

    public List<User> getUsers() {
        log.trace("Fetching all users.");
        return userRepo.findAll();
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    private Optional<User> findUserByUserId(String userId) {
        return userRepo.findByUserId(userId);
    }

    @Override
    public void save(User user) {
        userRepo.save(user);
    }

    @Override
    public void delete(User user) {
        userRepo.deleteById(user.getId());
    }
}
