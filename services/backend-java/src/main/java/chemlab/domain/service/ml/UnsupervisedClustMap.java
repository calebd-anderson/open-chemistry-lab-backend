package chemlab.domain.service.ml;

import chemlab.infrastructure.fastapiworker.ClusterMapRequest;
import chemlab.shared.responses.ClusterMapResponse;

import java.util.List;

public interface UnsupervisedClustMap {
    ClusterMapResponse testMl(List<ClusterMapRequest> data);
}
