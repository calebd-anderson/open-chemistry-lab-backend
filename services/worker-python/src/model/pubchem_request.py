from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_pascal

JSON_DATA = '''{
    "PropertyTable": {
        "Properties": [
            {
                "CID": 977,
                "MolecularFormula": "O2",
                "MolecularWeight": "31.999",
                "ConnectivitySMILES": "O=O",
                "InChIKey": "MYMOFIZGZYHOMD-UHFFFAOYSA-N",
                "Charge": 0,
                "Fingerprint2D": "AAADcQAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==",
                "Title": "Oxygen"
            }
        ]
    }
}'''

class PubChemFastformulaPropertiesResponse(BaseModel):
    model_config = ConfigDict(
        alias_generator=to_pascal,
        populate_by_name=True,
    )
    property_table: PubChemFastformulaProperties


class PubChemFastformulaProperties(BaseModel):
    model_config = ConfigDict(
        alias_generator=to_pascal,
        populate_by_name=True,
    )
    properties: list[PubChemFastformulaCidProperties]


class PubChemFastformulaCidProperties(BaseModel):
    model_config = ConfigDict(
        alias_generator=to_pascal,
        populate_by_name=True,
    )
    cid: int = Field(alias="CID")
    molecular_formula: str
    molecular_weight: str
    connectivity_smiles: str = Field(alias="ConnectivitySMILES")
    in_ch_i_key: str
    charge: int
    fingerprint_2d: str
    title: str

print(PubChemFastformulaPropertiesResponse.model_validate_json(JSON_DATA))
