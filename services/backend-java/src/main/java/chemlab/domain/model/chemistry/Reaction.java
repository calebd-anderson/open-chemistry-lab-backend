package chemlab.domain.model.chemistry;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Document(collection = "reactions")
@Data
public class Reaction {
    @MongoId
    private ObjectId id;
    private HashMap<String, Integer> elements;
    @Indexed(unique = true)
    private String formula;
    private String title;
    private Instant firstDiscoveredWhen;
    private String firstDiscoveredBy;
    private Instant lastDiscoveredWhen;
    private String lastDiscoveredBy;
    private int discoveredCount;

    public Reaction() {
    }

    public Reaction(HashMap<String, Integer> elements) {
        this.elements = elements;
        this.formula = createFormula();
    }

    private String createFormula() {
        StringBuilder formula = new StringBuilder();
        for (Map.Entry<String, Integer> entry : elements.entrySet()) {
            String key = entry.getKey();
            int val = entry.getValue();
            if (val == 1) {
                formula.append(key);
            } else {
                formula.append(key).append(val);
            }
        }
        return formula.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Reaction reaction = (Reaction) obj;
        return this.formula.equals(reaction.getFormula());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, formula);
    }
}
