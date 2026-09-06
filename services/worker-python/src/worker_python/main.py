from fastapi import FastAPI

from model.pubchem_request import PubChemFastformulaPropertiesResponse
from service.transform_service import DataTransformer

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.post("/transform/")
async def transform(item: PubChemFastformulaPropertiesResponse):
    data_transformer = DataTransformer(item)
    data_transformer.create_topology()
    data_transformer.create_composition()
    data_transformer.create_charge_indicators()
    data_transformer.create_mass()
    data_transformer.reduce_to_vectors()
    return data_transformer.X_data
