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
import java.util.Optional;

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
        this.formula = (elements != null) ? createFormula() : null;
    }

    public void setElements(HashMap<String, Integer> elements) {
        this.elements = elements;
        this.formula = (elements != null) ? createFormula() : null;
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

    /**
     * Handles a discovery event. If it's the first time, it initializes the
     * permanent discovery metadata. Always updates the 'last discovered' metadata.
     */
    public void recordDiscovery(String discoverer, Optional<String> title, Instant discoveryTime) {
        // 1. Always update the 'last discovery' metadata
        this.lastDiscoveredBy = discoverer;
        this.lastDiscoveredWhen = discoveryTime;
        this.discoveredCount++;

        // 2. If a title is provided, this is a 'First Discovery'
        title.ifPresent(newTitle -> {
            this.title = newTitle;
            this.firstDiscoveredBy = discoverer;
            this.firstDiscoveredWhen = discoveryTime;
        });
    }

    public void updateDiscovery(String discoverer, Instant discoveryTime) {
        this.lastDiscoveredBy = discoverer;
        this.lastDiscoveredWhen = discoveryTime;
        this.discoveredCount++;
    }

    public void incrementDiscoveredCount() {
        this.discoveredCount++;
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
