package chemlab.shared.responses;

import lombok.Data;

@Data
public class ClusterMapResponse {
    private double[][] embedding;
    private int[] labels;

    public ClusterMapResponse() {
    }

    public ClusterMapResponse(double[][] embedding, int[] labels) {
        this.embedding = embedding;
        this.labels = labels;
    }
}
