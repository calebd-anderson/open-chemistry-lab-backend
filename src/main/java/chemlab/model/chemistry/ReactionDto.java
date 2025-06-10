package chemlab.model.chemistry;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Data
public class ReactionDto {
    private ArrayList<Element> elements;
    private String userId;

    @JsonCreator
    public ReactionDto(ArrayList<Element> elements, String userId) {
        this.elements = elements;
        this.userId = userId;
    }

    public String getConcatPayload() {
        return getMappedPayload().entrySet().stream().map(entry -> entry.getKey() + entry.getValue()).collect(Collectors.joining());
    }

    public HashMap<String, Integer> getMappedPayload() {
        HashMap<String, Integer> molecule = new HashMap<>();
        for (Element d : elements) {
            molecule.put(d.getElement(), d.getNumberOfAtoms());
        }
        return molecule;
    }

    @lombok.Data
    public static class Element {
        private String element;
        private int numberOfAtoms;

        @JsonCreator
        public Element(String element, int numberOfAtoms) {
            this.element = element;
            this.numberOfAtoms = numberOfAtoms;
        }
    }
}
