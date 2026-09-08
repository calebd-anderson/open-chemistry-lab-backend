package chemlab.infrastructure.fastapiworker.service;

import chemlab.infrastructure.fastapiworker.ClusterMapRequest;
import chemlab.infrastructure.pubchem.PugApiResponse.FastformulaPropertiesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface FastApiWorkerService {
    List<ClusterMapRequest> analyzePubChemFastformulaProps(FastformulaPropertiesResponse pugApiResponse) throws JsonProcessingException;
}
