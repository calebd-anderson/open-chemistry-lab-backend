package chemlab.service.ml;

import chemlab.domain.service.ml.UnsupervisedClustMap;
import chemlab.shared.ClusterMapData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smile.clustering.DBSCAN;
import smile.data.DataFrame;
import smile.feature.transform.MaxAbsScaler;
import smile.manifold.TSNE;

@Service
@Slf4j
public class DefaultSmileMl implements UnsupervisedClustMap {
    public ClusterMapData testMl(double[][] data)  {
        // https://haifengl.github.io/data.html
        var df = DataFrame.of(data);
        // https://haifengl.github.io/feature.html
        var scaler = MaxAbsScaler.fit(df);
        var X = scaler.apply(df).toArray();

        // https://haifengl.github.io/clustering.html#dbscan
        int minPts = 3;
        double radius = 0.5;
        DBSCAN<double[]> model = DBSCAN.fit(X, minPts, radius);
        int[] labels = model.group();

        // https://haifengl.github.io/manifold.html#t-sne
        var tsne = TSNE.fit(X, new TSNE.Options(2, Math.min(5, X.length - 1), 200, 12, 550));
        double[][] embedding = tsne.coordinates();

        // Plot embedding[i] using labels[i] as the color.

        var clustMapData = new ClusterMapData(embedding, labels);
        return clustMapData;
    }
}
