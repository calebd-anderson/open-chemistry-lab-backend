package chemlab.model.chemistry;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "reaction")
@Data
public class Reaction {

    private final HashMap<String, Integer> elements;
    private String formula;
    private String title;

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

    public boolean equals(Reaction reaction) {
        return this.formula.equals(reaction.getFormula());
    }
}
