// a domain class
package chemlab.model.chemistry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Element {
	private String atomicNumber;									// atomic number: invariant, identifies the element, represents
																	// the number of protons in the nucleus of the atom  (becomes _id in mongo)
    private String symbol;											// symbol
	private String name;											// name
	private String atomicMass;										// mass number: varies, identifies the isotope, represents the sum of protons
                                                                    // and neutrons in the nucleus of the atom
    @JsonProperty("cPKHexColor")
	private String cPKHexColor;										// color
	private String electronConfiguration;
	private String electronegativity;								// valence, outer energy level electrons involved in covalent bond ?
	private String atomicRadius;
	private String ionizationEnergy;
	private String electronAffinity;
	private String oxidationStates;
	private String standardState;
	private String meltingPoint;
	private String boilingPoint;
	private String density;
	private String groupBlock;										// group/family: metal, metalloid, non-metal etc.
	private String tablePosition;
	private String yearDiscovered;
}
