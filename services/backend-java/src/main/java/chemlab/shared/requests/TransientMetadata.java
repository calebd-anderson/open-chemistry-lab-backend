package chemlab.shared.requests;

import lombok.Data;

@Data
public class TransientMetadata {
    private String stateless_ref_cid;
    private double stateless_relative_mass;
    private double stateless_relative_charge;
    private double stateless_tanimoto;
}
