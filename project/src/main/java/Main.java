import customCode.CustomNode;
import customCode.CustomTopology;
import io.jbotsim.ui.JViewer;

public class Main {
    public static void main(String[] args){
        CustomTopology topology = new CustomTopology();
        topology.setDefaultNodeModel(CustomNode.class);

        new JViewer(topology);
    }
}