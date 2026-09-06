import json

import numpy as np
import pytest


class MockData:
    def __init__(self):
        with open("tests/mock_pug_resp.json", encoding='utf-8') as file:
            data = json.load(file)
            self.raw_json = file

        self.X_data = np.array([list(cid.values()) for cid in data["PropertyTable"]["Properties"]])


# Arrange
@pytest.fixture
def mock_data():
    return MockData()


def test_fruit_salad(mock_data):
    # Act
    mock_data = mock_data

    # Assert
    assert len(list(mock_data.X_data)) == 13
