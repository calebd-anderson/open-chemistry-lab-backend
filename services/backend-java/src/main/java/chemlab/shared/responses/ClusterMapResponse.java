package chemlab.shared.responses;

import chemlab.infrastructure.fastapiworker.ClusterMapRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClusterMapResponse {
    List<Node> nodes;
    List<Link> links;

    public ClusterMapResponse() {
    }

    public ClusterMapResponse(List<ClusterMapRequest> data, int[] labels) {
        this.nodes = new ArrayList<>();
        this.links = new ArrayList<>();
        // there should be as many labels as there are data-s
        for (int i = 0; i < labels.length; i++) {
            // create the nodes
            Node node = new Node();
            node.group = labels[i];
            node.id = data.get(i).getCid();
            this.nodes.add(node);

            Link link = new Link();
            link.source = data.get(i).getCid();
            link.target = data.get(i).getTransientMetadata().getStatelessRefCid();
            link.value = data.get(i).getTransientMetadata().getStatelessTanimoto();
            this.links.add(link);
        }
    }
}

@Data
class Node {
    int id;
    int group;
}

@Data
class Link {
    int source;
    int target;
    double value;
}
