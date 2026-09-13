package chemlab.infrastructure.persistence.user;

import chemlab.domain.model.user.User;
import chemlab.domain.repository.RegisteredUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements RegisteredUserRepository {
    @Autowired
    private final SpringDataUserRepository mongoRepository;

    @Override
    public User save(User user) {
        return mongoRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return mongoRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId);
    }

    @Override
    public void deleteById(String userId) {
        mongoRepository.deleteById(userId);
    }

    @Override
    public List<User> findAll() {
        return mongoRepository.findAll();
    }
}
