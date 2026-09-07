package chemlab.shared;

import lombok.Data;

import java.time.Instant;
import java.util.HashMap;

@Data
public class ReactionResponse {
    private HashMap<String, Integer> elements;
    private String formula;
    private String title;
    private Instant firstDiscoveredWhen;
    private String firstDiscoveredBy;
    private Instant lastDiscoveredWhen;
    private String lastDiscoveredBy;
    private int discoveredCount;
}
