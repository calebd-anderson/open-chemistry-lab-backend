package chemlab.shared.requests;

import lombok.Data;

@Data
public class ClusterMapRequest {
    private int cid;
    private String title;
    private String InChIKey;
    private double[] featureVector;
    private TransientMetadata transientMetadata;
}

