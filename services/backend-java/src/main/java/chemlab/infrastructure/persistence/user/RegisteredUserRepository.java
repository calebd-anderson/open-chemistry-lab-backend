package chemlab.infrastructure.persistence.user;

import chemlab.domain.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    User findByUsername(String username);
    User findByUserId(String userId);
}
