package chemlab.shared.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class ReactionRequest {
    private ArrayList<Element> elements;

    @JsonCreator
    public ReactionRequest(ArrayList<Element> elements) {
        this.elements = elements;
    }

    public HashMap<String, Integer> getMappedPayload() {
        HashMap<String, Integer> molecule = new HashMap<>();
        for (Element d : elements) {
            molecule.put(d.getSymbol(), d.getNumberOfAtoms());
        }
        return molecule;
    }
}
