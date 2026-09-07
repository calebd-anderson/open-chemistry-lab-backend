package chemlab.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransientMetadata {
    @JsonProperty("stateless_ref_cid")
    private int statelessRefCid;
    @JsonProperty("stateless_relative_mass")
    private double statelessRelativeMass;
    @JsonProperty("stateless_relative_charge")
    private double statelessRelativeCharge;
    @JsonProperty("stateless_tanimoto")
    private double statelessTanimoto;
}
