package chemlab.shared.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class Element {
    private String symbol;
    private int numberOfAtoms;

    @JsonCreator
    public Element(String symbol, int numberOfAtoms) {
        this.symbol = symbol;
        this.numberOfAtoms = numberOfAtoms;
    }
}
