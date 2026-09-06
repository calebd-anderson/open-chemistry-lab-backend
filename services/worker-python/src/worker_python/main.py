from fastapi import FastAPI

from model.pubchem_request import PubChemFastformulaPropertiesResponse

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.post("/transform/")
async def transform(item: PubChemFastformulaPropertiesResponse):
    
    return item

