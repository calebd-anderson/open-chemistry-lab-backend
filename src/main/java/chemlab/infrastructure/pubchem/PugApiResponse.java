package chemlab.infrastructure.pubchem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PugApiResponse {
	private final PropertyTableObj PropertyTable;

	@JsonCreator
	PugApiResponse(@JsonProperty("PropertyTable") PropertyTableObj PropertyTable) {
		this.PropertyTable = PropertyTable;
	}

    public String getFirstPropertyTitle() {
		return this.PropertyTable.Properties.getFirst().getTitle();
	}

	@Getter
    static class PropertyTableObj {
		private final ArrayList<Properties> Properties;
		
		@JsonCreator
		public PropertyTableObj (@JsonProperty("Properties") ArrayList<Properties> Properties) {
			this.Properties = Properties;
		}
	}
	
	@Getter
	static class Properties {
		private final int CID;
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
}
