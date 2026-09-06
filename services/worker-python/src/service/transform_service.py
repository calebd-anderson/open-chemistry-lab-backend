from rdkit import Chem, DataStructs
from rdkit.Chem import AllChem

import numpy as np

class DataTransformer:
    def __init__(self, data: object):
        self.data = data