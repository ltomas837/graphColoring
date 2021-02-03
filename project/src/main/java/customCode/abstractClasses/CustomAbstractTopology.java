package customCode.abstractClasses;

import io.jbotsim.core.Color;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CustomAbstractTopology extends Topology{
    //region Overrides
    @Override
    public void start() {
        List<Node> nodes = this.getNodes();
        int numberOfNodes = nodes.size();
        CustomAbstractNode.setNumberOfVertices(numberOfNodes);
        CustomAbstractNode.setMaxDegree(getMaxNumberOfLinks(nodes));
        CustomAbstractNode.setAvailableColors(getListOfColors(2*numberOfNodes));
        for (int i = 0; i < numberOfNodes; i++) {
            nodes.get(i).setID(i);
        }

        System.out.println(CustomAbstractNode.MAX_DEGREE);

        super.start();
    }
    //endregion

    //region Intialization
    protected int getMaxNumberOfLinks(List<Node> nodesList){
        int maxLinks = 0;
        for (Node node : nodesList){
            int currentNodeLinks = node.getLinks().size();
            if(currentNodeLinks > maxLinks){
                maxLinks = currentNodeLinks;
            }
        }

        return maxLinks;
    }

    private List<Color> getListOfColors(double n){
        int stepSize = 255;
        int nbSteps = 1;
        if(n > 7){
            n = n;
            stepSize = (int) (255 / Math.ceil(n / 7));
            nbSteps = (int) Math.ceil(n / 7);
        }

        ArrayList<Color> colors = new ArrayList<Color>();
        int currentR = 0;
        int currentG = 0;
        int currentB = 0;
        colors.add(new Color(currentR, currentG, currentB));

        for (int i = 0; i < nbSteps; i++) {
            colors.add(new Color(currentR + stepSize, currentG, currentB));
            colors.add(new Color(currentR + stepSize, currentG + stepSize, currentB));
            colors.add(new Color(currentR , currentG + stepSize, currentB));
            colors.add(new Color(currentR, currentG + stepSize, currentB + stepSize));
            colors.add(new Color(currentR, currentG, currentB + stepSize));
            colors.add(new Color(currentR + stepSize, currentG, currentB + stepSize));
            colors.add(new Color(currentR + stepSize, currentG + stepSize, currentB + stepSize));
            currentR += stepSize;
            currentG += stepSize;
            currentB += stepSize;
        }

        Collections.shuffle(colors);
        Collections.shuffle(colors);
        return colors.subList(0, (int) n);
    }
    //endregion

    //region Ending
    public void verifyColoration(){
        System.out.println("Verifying the coloration obtained...");
        boolean error = false;
        for (Node node : getNodes()){
            if(node.getNeighbors().stream().anyMatch(
                    n->((CustomAbstractNode)n).finalColor == ((CustomAbstractNode)node).finalColor)){
                error = true;
                System.out.println("ERROR : Node " + node.getID() + " has at least one neighbor with the same color !");
            }
        }

        if(!error){
            System.out.println("Found no error, the coloration is a proper coloration.");
        }
    }
    //endregion
}
