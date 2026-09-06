from fastapi.testclient import TestClient

from worker_python.main import app

client = TestClient(app)

JSON_DATA = {
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
}

def test_root():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Hello World"}


def test_transform():
    response = client.post("/transform/", json=JSON_DATA)
    assert response.status_code == 200
    assert response.json() == JSON_DATA
