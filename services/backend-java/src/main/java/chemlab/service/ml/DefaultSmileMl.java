package chemlab.service.ml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smile.clustering.DBSCAN;

@Service
@Slf4j
public class DefaultSmileMl {
    public void testMl(double[][] x)  {
        int minPts = 3;
        double radius = 0.5;

        DBSCAN<double[]> model = DBSCAN.fit(x, minPts, radius);
        int[] labels = model.group();

        for (int i = 0; i < labels.length; i++) {
            log.info("row {} -> cluster {}", i, labels[i]);
        }
    }
}
