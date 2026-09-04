package chemlab.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class ReactionRequest {
    private ArrayList<Element> elements;
    private String userId;

    @JsonCreator
    public ReactionRequest(ArrayList<Element> elements, String userId) {
        this.elements = elements;
        this.userId = userId;
    }

    public HashMap<String, Integer> getMappedPayload() {
        HashMap<String, Integer> molecule = new HashMap<>();
        for (Element d : elements) {
            molecule.put(d.getSymbol(), d.getNumberOfAtoms());
        }
        return molecule;
    }
}
