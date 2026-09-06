package chemlab.shared;

import lombok.Data;

@Data
public class ClusterMapData {
    private double[][] embedding;
    private int[] labels;

    public ClusterMapData() {
    }

    public ClusterMapData(double[][] embedding, int[] labels) {
        this.embedding = embedding;
        this.labels = labels;
    }
}
