package chemlab.repositories;

import chemlab.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUserRepository extends MongoRepository<User, String> {
    User findRegisteredUserByEmail(String email);

    User findRegisteredUserByUsername(String username);

    User findRegisteredUserByUserId(String userId);

    // getAllUsers first 100 da da da
    //	@Query("")
    //	List<RegisteredUser> getUsers =
}
