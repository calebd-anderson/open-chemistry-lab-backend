package shared;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PugApiDto {
	private PropertyTableObj PropertyTable;

	public PugApiDto() {
		this.PropertyTable = new PropertyTableObj(new ArrayList<Properties>());
	}

	@JsonCreator
	public PugApiDto(@JsonProperty("PropertyTable") PropertyTableObj PropertyTable) {
		this.PropertyTable = PropertyTable;
	}
	
	public PropertyTableObj getPropertyTable() {
		return this.PropertyTable;
	}
	
	public String getFirstPropertyTitle() {
		return this.PropertyTable.Properties.get(0).getTitle();
	}
	
	public void initializePropertyTableObj() {
		this.PropertyTable = new PropertyTableObj(new ArrayList<Properties>());
	}
	
	public void appendToPropertyTableObj(int CID, String molecularFormula, String molecularWeight, String title) {
		this.PropertyTable.addProperties(new Properties(CID, molecularFormula, molecularWeight, title));
	}
//	public ArrayList<Properties> getProperties() {
//		return this.properties;
//	}
//	
//	public int getFirstPropertyCID() {
//		return this.firstPropertyCID;
//	}
//	
//	public String getFirstPropertyMolecularFormula() {
//		return this.firstPropertyMolecularFormula;
//	}
//	
//	public String getFirstPropertyMolecularWeight() {
//		return this.firstPropertyMolecularWeight;
//	}
//	
//	public String getFirstPropertyTitle() {
//		return this.firstPropertyTitle;
//	}
	
	static class PropertyTableObj {
		private final ArrayList<Properties> Properties;
		
		@JsonCreator
		public PropertyTableObj (@JsonProperty("Properties") ArrayList<Properties> Properties) {
			this.Properties = Properties;
		}
		
		public ArrayList<Properties> getProperties () {
			return this.Properties;
		}
		
		public void addProperties(Properties properties) {
			this.Properties.add(properties);
		}
	}
	
	
	static class Properties {
		private final int CID;
		private final String MolecularFormula;
		private final String MolecularWeight;
		private final String Title;
		
		@JsonCreator
		public Properties(@JsonProperty("CID") int CID, @JsonProperty("MolecularFormula") String MolecularFormula, @JsonProperty("MolecularWeight") String MolecularWeight, @JsonProperty("Title") String Title) {
			this.CID = CID;
			this.MolecularFormula = MolecularFormula;
			this.MolecularWeight = MolecularWeight;
			this.Title = Title;
		}
		
		public int getCID() {
			return this.CID;
		}
		
		public String getMolecularFormula() {
			return this.MolecularFormula;
		}
		
		public String getMolecularWeight() {
			return this.MolecularWeight;
		}
		
		public String getTitle() {
			return this.Title;
		}
	}

}
