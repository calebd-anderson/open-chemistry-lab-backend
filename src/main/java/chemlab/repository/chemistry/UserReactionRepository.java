package chemlab.repository.chemistry;

import chemlab.model.chemistry.UserReactionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReactionRepository extends MongoRepository<UserReactionDocument, String> {
    Page<UserReactionDocument> findByUserId(String userId, Pageable pageable);

    long countByUserId(String userId);
}

