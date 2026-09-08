package chemlab.infrastructure.fastapiworker.service;

import chemlab.infrastructure.fastapiworker.ClusterMapRequest;
import chemlab.infrastructure.pubchem.PugApiResponse.FastformulaPropertiesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class DefaultFastApiWorkerService implements FastApiWorkerService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.fastapi-worker}")
    private String fastapiWorkerUrl;

    @Override
    public List<ClusterMapRequest> analyzePubChemFastformulaProps(FastformulaPropertiesResponse pugApiResponse) throws JsonProcessingException {

        String url = fastapiWorkerUrl + "/transform/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.trace("Sending request to FastAPI Worker: {}", url);
        ResponseEntity<ClusterMapRequest[]> fastApiResponse = restTemplate.postForEntity(url, pugApiResponse, ClusterMapRequest[].class, headers);

        assert fastApiResponse.getBody() != null;
        List<ClusterMapRequest> result = Arrays.asList(fastApiResponse.getBody());

        return result;
    }
}
