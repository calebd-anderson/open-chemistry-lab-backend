package chemlab.model.shared;

import chemlab.model.chemistry.Element;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Data
public class ReactionRequest {
    private ArrayList<Element> elements;
    private String userId;

    @JsonCreator
    public ReactionRequest(ArrayList<Element> elements, String userId) {
        this.elements = elements;
        this.userId = userId;
    }

    public String getConcatPayload() {
        return getMappedPayload().entrySet().stream().map(entry -> entry.getKey() + entry.getValue()).collect(Collectors.joining());
    }

    public HashMap<String, Integer> getMappedPayload() {
        HashMap<String, Integer> molecule = new HashMap<>();
        for (Element d : elements) {
            molecule.put(d.getSymbol(), d.getNumberOfAtoms());
        }
        return molecule;
    }
}
