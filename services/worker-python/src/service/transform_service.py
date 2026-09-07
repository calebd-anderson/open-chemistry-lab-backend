import numpy as np
from numpy.typing import NDArray
from pydantic import TypeAdapter
from rdkit import Chem, DataStructs
from rdkit.Chem import AllChem

from model.pubchem_features_response import CompoundResponse, TransientMetadata
from model.pubchem_request import (
    PubChemFastformulaCidProperties,
    PubChemFastformulaPropertiesRequest,
)

# type CompoundResponsesByCid = dict[int, list[CompoundResponse]]

class DataTransformer:
    def __init__(self, data: PubChemFastformulaPropertiesRequest):
        self.cids: list[PubChemFastformulaCidProperties] = data.property_table.properties
        # initalize a temporary matrix that will hold the features
        # to later be transformed into a single vector per cid
        self.feature_vectors: list[list[NDArray]] = [[] for _ in self.cids]
        self.partial_metadata: dict[int, dict] = {}
        # self.responses: dict[int, CompoundResponse] = {}
        self.responses: list[CompoundResponse] = []


    def initialize_metadata(self):
        for cid_idx, cid in enumerate(self.cids):
            self.responses[cid_idx].cid = cid.cid
            self.responses[cid_idx].title = cid.title
            self.responses[cid_idx].in_ch_i_key = cid.in_ch_i_key
            self.responses[cid_idx].features_vector = []

            self.partial_metadata[cid.cid] = {
                "stateless_ref_cid": self.cids[0].cid
            }


    def create_topology(self):
        for cid_idx, cid in enumerate(self.cids):
            current_mol = Chem.MolFromSmiles(cid.connectivity_smiles)
            fpgen = AllChem.GetMorganGenerator(radius=2, fpSize=2048)
            current_fp = fpgen.GetFingerprint(current_mol)
            # Convert the bit-string to a numpy array of bits
            fp_bits = np.array(list(current_fp.ToBitString()), dtype=np.int8)
            self.feature_vectors[cid_idx].append(fp_bits)

            ref_mol = Chem.MolFromSmiles(self.cids[0].connectivity_smiles)
            ref_fp = fpgen.GetFingerprint(ref_mol)

            chem_similarity = DataStructs.TanimotoSimilarity(ref_fp, current_fp)
            self.partial_metadata[cid.cid]["stateless_tanimoto"] = chem_similarity



    def create_composition(self):
        for cid_idx, cid in enumerate(self.cids):
            charge_template = np.zeros(118, dtype=np.int8)
            # for lack of a better way to enumerate the elements
            # i.e. based on the 'MolecularFormula'
            m = Chem.MolFromSmiles(cid.connectivity_smiles)
            for atom in m.GetAtoms():
                template_idx = atom.GetAtomicNum()-1
                charge_template[template_idx] += 1
            self.feature_vectors[cid_idx].append(charge_template)


    @staticmethod
    def get_charge_indicators(charge) -> np.ndarray:
        is_positive = 1 if charge > 0 else 0
        is_negative = 1 if charge < 0 else 0
        return np.array([float(is_positive), float(is_negative)])

    def create_charge_indicators(self):
        for cid_idx, cid in enumerate(self.cids):
            current_cid_charge = float(cid.charge)
            target_cid_charge_indicators = self.get_charge_indicators(current_cid_charge)
            self.feature_vectors[cid_idx].append(target_cid_charge_indicators)

            ref_cid_charge = float(self.cids[0].charge)
            delta_z = float(current_cid_charge - ref_cid_charge)
            self.partial_metadata[cid.cid]["stateless_relative_charge"] = delta_z


    def create_mass(self):
        for cid_idx, cid in enumerate(self.cids):
            current_cid_mass = np.array([float(cid.molecular_weight)])
            self.feature_vectors[cid_idx].append(current_cid_mass)

            ref_cid_mass = float(self.cids[0].molecular_weight)
            delta_mass = current_cid_mass - ref_cid_mass
            self.partial_metadata[cid.cid]["stateless_relative_mass"] = delta_mass


    def reduce_to_vectors(self) -> list[CompoundResponse]:
        for cid_idx, cid in enumerate(self.cids):
            # first transform each item in the matrix to a np array
            combined_features = np.array(
                [
                    self.feature_vectors[cid_idx][0],
                    self.feature_vectors[cid_idx][1],
                    self.feature_vectors[cid_idx][2],
                    self.feature_vectors[cid_idx][3],
                ],
                dtype=object,
            )
            # then concatinate each item so it is a single vector
            # and set it to the features_vector of the cid
            self.responses[cid_idx].features_vector = np.concatenate(
                combined_features
            ).tolist()

            metadata = TransientMetadata.model_validate(self.partial_metadata[cid.cid])
            self.responses[cid_idx].transient_meta_data = metadata

        adapter = TypeAdapter(list[CompoundResponse])
        return adapter.validate_python(self.responses)
