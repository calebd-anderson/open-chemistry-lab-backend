package chemlab.domain.service.ml;

import chemlab.shared.requests.ClusterMapRequest;
import chemlab.shared.responses.ClusterMapData;

import java.util.List;

public interface UnsupervisedClustMap {
    ClusterMapData testMl(List<ClusterMapRequest> data);
}
