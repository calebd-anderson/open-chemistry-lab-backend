package chemlab.repository.user;

import chemlab.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUserRepository extends MongoRepository<User, String> {
    User findRegisteredUserByEmail(String email);

    User findRegisteredUserByUsername(String username);

    User findRegisteredUserByUserId(String userId);
}
