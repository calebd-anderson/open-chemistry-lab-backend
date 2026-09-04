package chemlab.infrastructure.pubchem.PugApiResponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FastformulaPropertiesResponse {
	private final PropertyTableObj propertyTableObj;

	@JsonCreator
	FastformulaPropertiesResponse(@JsonProperty("PropertyTable") PropertyTableObj propertyTableObj) {
		this.propertyTableObj = propertyTableObj;
	}

	record PropertyTableObj(ArrayList<Properties> properties) {
		@JsonCreator
		PropertyTableObj(@JsonProperty("Properties") ArrayList<Properties> properties) {
			this.properties = properties;
		}
	}
	
	@Getter
	static class Properties {
		public final int CID;
		private final String MolecularFormula;
		private final String MolecularWeight;
		private final String Title;
		
		@JsonCreator
		public Properties(@JsonProperty("CID") int CID,
						  	@JsonProperty("MolecularFormula")
						  	String MolecularFormula,
						  	@JsonProperty("MolecularWeight")
						  	String MolecularWeight,
						  	@JsonProperty("Title")
						  	String Title) {
			this.CID = CID;
			this.MolecularFormula = MolecularFormula;
			this.MolecularWeight = MolecularWeight;
			this.Title = Title;
		}
	}

	public String getFirstPropertyTitle() {
		return this.propertyTableObj.properties.getFirst().getTitle();
	}

//	public int getCid() {
//		return this.propertyTableObj.properties.getFirst().CID;
//	}
}
