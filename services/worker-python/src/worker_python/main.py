from fastapi import FastAPI
from rdkit import Chem, DataStructs
from rdkit.Chem import AllChem

app = FastAPI()


@app.get("/")
async def root():
    ms = [Chem.MolFromSmiles('CCOC'), Chem.MolFromSmiles('CCO'), Chem.MolFromSmiles('COC')]
    fpgen = AllChem.GetRDKitFPGenerator() # noqa
    fps = [fpgen.GetFingerprint(x) for x in ms]
    # DataStructs.TanimotoSimilarity(fps[0],fps[1])
    # DataStructs.TanimotoSimilarity(fps[0],fps[2])
    # DataStructs.TanimotoSimilarity(fps[1],fps[2])
    return {"message": "Hello World", "smiles": DataStructs.TanimotoSimilarity(fps[1],fps[2])}
