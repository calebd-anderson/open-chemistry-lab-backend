from pydantic import BaseModel, TypeAdapter


class CompoundResponse(BaseModel):
    cid: int
    title: str
    in_ch_i_key: str
    features_vector: list[float]
    transient_meta_data: TransientMetadata | None = None


class TransientMetadata(BaseModel):
    stateless_ref_cid: int
    stateless_relative_mass: float
    stateless_relative_charge: float
    stateless_tanimoto: float


# JSON_DATA = [
#     {
#         "cid": 1234,
#         "title": "abc123",
#         "in_ch_i_key": "12345abcdef",
#         "stateless_ref_cid": 123456,
#         "stateless_relative_mass": 1234,
#         "stateless_relative_charge": 12.10,
#         "stateless_tanimoto": 0.25,
#         "features_vector": [
#             0,
#             0,
#             0,
#             0,
#             0,
#             0,
#         ]
#     }
# ]

# compounds = [CompoundResponse.model_validate(item) for item in JSON_DATA]

# adapter = TypeAdapter(list[CompoundResponse])
# compounds = adapter.validate_python(JSON_DATA)

# compounds = adapter.validate_json(json_string)