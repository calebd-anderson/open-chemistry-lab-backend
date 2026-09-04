from fastapi import FastAPI
from rdkit import Chem

app = FastAPI()


@app.get("/")
async def root():
    m = Chem.MolFromSmiles('Cc1ccccc1')
    return {"message": "Hello World", "smiles": Chem.MolToSmiles(m)}