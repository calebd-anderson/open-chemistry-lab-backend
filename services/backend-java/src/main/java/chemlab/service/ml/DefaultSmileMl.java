package chemlab.service.ml;

import chemlab.domain.service.ml.UnsupervisedClustMap;
import chemlab.shared.requests.ClusterMapRequest;
import chemlab.shared.responses.ClusterMapData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smile.clustering.DBSCAN;
import smile.data.DataFrame;
import smile.feature.transform.MaxAbsScaler;
import smile.manifold.TSNE;

import java.util.List;

@Service
@Slf4j
public class DefaultSmileMl implements UnsupervisedClustMap {

    /**
     * Computes and logs the min/max statistics for each column in a 2D array.
     * Similar to NumPy's .min(axis=0) and .max(axis=0).
     */
    private void logDataFrameStats(String label, double[][] data) {
        if (data == null || data.length == 0) {
            log.info("{} - No data to analyze", label);
            return;
        }

        int numColumns = data[0].length;
        int numRows = data.length;
        StringBuilder minValues = new StringBuilder("[");
        StringBuilder maxValues = new StringBuilder("[");

        for (int col = 0; col < numColumns; col++) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            for (int row = 0; row < numRows; row++) {
                double value = data[row][col];
                if (value < min) min = value;
                if (value > max) max = value;
            }

            if (col > 0) {
                minValues.append(", ");
                maxValues.append(", ");
            }
            minValues.append(String.format("%.6f", min));
            maxValues.append(String.format("%.6f", max));
        }

        minValues.append("]");
        maxValues.append("]");

        log.info("{} - Min values per column: {}", label, minValues);
        log.info("{} - Max values per column: {}", label, maxValues);
    }

    public ClusterMapData testMl(List<ClusterMapRequest> data)  {
        // https://haifengl.github.io/data.html
        double[][] featureVectors = data.stream()
                .map(ClusterMapRequest::getFeatureVector)
                .toArray(double[][]::new);
        var df = DataFrame.of(featureVectors);

        // Log statistics before scaling
//        logDataFrameStats("BEFORE MaxAbsScaler", data);

        // https://haifengl.github.io/feature.html
        var scaler = MaxAbsScaler.fit(df);
        var X = scaler.apply(df).toArray();
        log.info("Data shape: {} rows, {} columns", featureVectors.length, featureVectors[0].length);
        log.info("Scaled shape: {} rows, {} columns", X.length, X[0].length);

        // Log statistics after scaling
//        logDataFrameStats("AFTER MaxAbsScaler", X);

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
