package chemlab.infrastructure.pubchem.PugApiResponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
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
	
	@Data
	static class Properties {
		public final int CID;
		private final String MolecularFormula;
		private final String MolecularWeight;
		private final String Title;
		@JsonProperty("InChIKey")
		private final String InChIKey;
		private final int Charge;
		@JsonProperty("Fingerprint2D")
		private final String Fingerprint2D;
		private final String ConnectivitySMILES;
	}

	public String getFirstPropertyTitle() {
		return this.propertyTableObj.properties.getFirst().getTitle();
	}
}
