import customCode.CustomCycleNode;
import customCode.CustomCycleTopology;
import io.jbotsim.ui.JViewer;

public class CycleMain {
    public static void main(String[] args){
        CustomCycleTopology topology = new CustomCycleTopology();
        topology.setDefaultNodeModel(CustomCycleNode.class);

        new JViewer(topology);
    }
}