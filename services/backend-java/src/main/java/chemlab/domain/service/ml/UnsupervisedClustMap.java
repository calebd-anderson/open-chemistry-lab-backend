package chemlab.domain.service.ml;

import chemlab.shared.ClusterMapData;

public interface UnsupervisedClustMap {
    ClusterMapData testMl(double[][] data);
}
