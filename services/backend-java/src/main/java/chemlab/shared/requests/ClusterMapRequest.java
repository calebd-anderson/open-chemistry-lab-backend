package chemlab.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClusterMapRequest {
    private int cid;
    private String title;
    @JsonProperty("in_ch_i_key")
    private String InChIKey;
    @JsonProperty("features_vector")
    private double[] featuresVector;
    @JsonProperty("transient_meta_data")
    private TransientMetadata transientMetadata;
}

