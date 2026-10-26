package chemlab.domain.model.chemistry;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "famous_molecules")
@Data
public class FamousMolecule {
    @MongoId
    ObjectId id;
    @Indexed(unique = true)
    String formula;
    String name;
    int CID;
}
