package chemlab.domain.model.chemistry;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class ReactionTest {

    @Test
    void constructor_shouldSetFormula() {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        Reaction reaction = new Reaction(elements);
        assertEquals("H2O", reaction.getFormula());
    }

    @Test
    void setElements_shouldUpdateFormula() {
        HashMap<String, Integer> elements = new HashMap<>();
        elements.put("H", 2);
        elements.put("O", 1);
        Reaction reaction = new Reaction(elements);
        assertEquals("H2O", reaction.getFormula());

        HashMap<String, Integer> newElements = new HashMap<>();
        newElements.put("Na", 1);
        newElements.put("Cl", 1);
        reaction.setElements(newElements);
        assertEquals("NaCl", reaction.getFormula());
    }

    @Test
    void setElements_withNull_shouldSetFormulaToNull() {
        Reaction reaction = new Reaction(new HashMap<>());
        reaction.setElements(null);
        assertNull(reaction.getFormula());
    }
}
