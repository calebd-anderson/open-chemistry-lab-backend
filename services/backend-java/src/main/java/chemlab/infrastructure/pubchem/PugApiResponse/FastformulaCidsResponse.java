package chemlab.infrastructure.pubchem.PugApiResponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//{
//        "IdentifierList": {
//        "CID": [
//            977,
//            5359597,
//            10313042,
//            10197606,
//            5460605,
//            11170911,
//            5289087,
//            73357760,
//            10197605,
//            10290726,
//            16225401,
//            16225402,
//            24996038
//        ]
//    }
//}
public class FastformulaCidsResponse {
    private final IdentifierList identifierList;

    @JsonCreator
    FastformulaCidsResponse(@JsonProperty("IdentifierList") IdentifierList identifierList) {
        this.identifierList = identifierList;
    }

    record IdentifierList(int[] CID) {
        @JsonCreator
        IdentifierList(@JsonProperty("CID") int[] CID) {
            this.CID = CID;
        }
    }

    public int[] getCids() {
        return this.identifierList.CID;
    }
}
