from typing import Any

import numpy as np
from numpy.typing import NDArray
from rdkit import Chem
from rdkit.Chem import AllChem

from model.pubchem_request import (
    JSON_DATA,
    PubChemFastformulaCidProperties,
    PubChemFastformulaPropertiesResponse,
)


class DataTransformer:
    def __init__(self, data: PubChemFastformulaPropertiesResponse):
        self.cids: list[PubChemFastformulaCidProperties] = data.property_table.properties
        # initalize the matrix of size len(self.cids) that will hold the vectors
        # self.X_data = [[] for _ in enumerate(self.cids)]
        # self.X_data = [np.empty(0) for _ in enumerate(self.cids)]
        # Entries contain feature arrays while being built, then become a single
        # vector in reduce_to_vectors.
        self.X_data: list[Any] = [[] for _ in self.cids]

    def create_topology(self):
        for cid_idx, cid in enumerate(self.cids):
            current_mol = Chem.MolFromSmiles(cid.connectivity_smiles)
            fpgen = AllChem.GetMorganGenerator(radius=2, fpSize=2048)
            current_fp = fpgen.GetFingerprint(current_mol)
            # Convert the bit-string to a numpy array of bits
            fp_bits = np.array(list(current_fp.ToBitString()), dtype=np.int8)
            self.X_data[cid_idx].append(fp_bits)


    def create_composition(self):
        for cid_idx, cid in enumerate(self.cids):
            charge_template = np.zeros(118, dtype=np.int8)
            # for lack of a better way to enumerate the elements
            # i.e. based on the 'MolecularFormula'
            m = Chem.MolFromSmiles(cid.connectivity_smiles)
            for atom in m.GetAtoms():
                charge_template[atom.GetAtomicNum()-1] += 1
            self.X_data[cid_idx].append(charge_template)


    @staticmethod
    def get_charge_indicators(charge) -> np.ndarray:
        is_positive = 1 if charge > 0 else 0
        is_negative = 1 if charge < 0 else 0
        return np.array([float(is_positive), float(is_negative)])

    def create_charge_indicators(self):
        for cid_idx, cid in enumerate(self.cids):
            target_cid_charge_indicators = self.get_charge_indicators(float(cid.charge))
            self.X_data[cid_idx].append(target_cid_charge_indicators)


    def create_mass(self):
        for cid_idx, cid in enumerate(self.cids):
            current_cid_mass = np.array([float(cid.molecular_weight)])
            self.X_data[cid_idx].append(current_cid_mass)


    def reduce_to_vectors(self):
        for cid_idx, cid in enumerate(self.cids):
            # first transform each item in the matrix a np array
            cid_obj = np.array([self.X_data[cid_idx][0], self.X_data[cid_idx][1],
                                self.X_data[cid_idx][2], self.X_data[cid_idx][3]], dtype=object)
            # then concatinate each item so it is a single vector
            vector = np.concatenate(cid_obj)
            self.X_data[cid_idx] = vector

        # finally, serialize the matrix of vectors
        self.X_data = np.array(self.X_data).tolist()
