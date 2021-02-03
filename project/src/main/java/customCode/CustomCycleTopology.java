package customCode;

import customCode.abstractClasses.CustomAbstractTopology;
import io.jbotsim.core.Link;
import io.jbotsim.core.Node;

import java.util.List;

public class CustomCycleTopology extends CustomAbstractTopology {
    //region Overrides
    @Override
    public void start() {
        List<Node> nodes = this.getNodes();
        super.start();

        if(nodes.stream().anyMatch(n -> n.getLinks().size() != 2)){
            System.out.println("This code is made only for cycles !");
        }
        else {
            computeOneOrientationForCycles(nodes);
        }
    }
    //endregion

    //region Initialization
    private void computeOneOrientationForCycles(List<Node> nodes){
        Node currentNode = nodes.get(0);
        while (((CustomCycleNode) currentNode).getPred() == null ||
                ((CustomCycleNode) currentNode).getSucc() == null) {

            Link link = currentNode.getLinks().get(0);
            if (((CustomCycleNode) link.source).getSucc() == currentNode ||
                    ((CustomCycleNode) link.destination).getSucc() == currentNode) {
                link = currentNode.getLinks().get(1);
            }

            Node nextNode = link.source.equals(currentNode) ? link.destination : link.source;
            ((CustomCycleNode) currentNode).setSucc(nextNode);
            ((CustomCycleNode) nextNode).setPred(currentNode);

            currentNode = nextNode;
        }
    }
    //endregion
}
