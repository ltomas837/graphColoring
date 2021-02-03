package customCode.abstractClasses;

import customCode.utils.CustomMessageContent;
import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class CustomAbstractNode extends Node{
    protected static int NUMBER_OF_VERTICES;
    protected static int MAX_DEGREE;
    protected static List<Color> AVAILABLE_COLORS;
    private static List<Boolean> NODES_FINISHED = new ArrayList<>();
    private static List<Integer> NODES_COLOR = new ArrayList<>();

    protected int nbOfRounds;
    protected int finalColor;

    //region Overrides
    @Override
    public void onStart() {
        super.onStart();
        nbOfRounds = 0;
        NODES_FINISHED.add(getID(), false);
        finalColor = getID();
    }

    @Override
    public void onPreClock() {
        super.onPreClock();
        nbOfRounds += 1;
    }

    @Override
    public void onPostClock() {
        super.onPostClock();
        setLabel("ID : " + getID() + " - COLOR : " + finalColor);
        setColor(AVAILABLE_COLORS.get(finalColor));
    }
    //endregion

    //region Setters
    public static void setNumberOfVertices(int numberOfVertices) {
        NUMBER_OF_VERTICES = numberOfVertices;
    }

    public static void setAvailableColors(List<Color> availableColors) {
        AVAILABLE_COLORS = availableColors;
    }

    public static void setMaxDegree(int maxDegree) {
        MAX_DEGREE = maxDegree;
    }
    //endregion

    //region Distributed Algorithm Methods
    protected int binaryLog(int value){
        return (int) (Math.ceil(Math.log(value) / Math.log(2) - 1e-10) + 1e-10);
    }

    protected boolean[] intToBinary(int value){
        char[] bitsAsCharArray = Integer.toBinaryString(value).toCharArray();
        boolean[] result = new boolean[binaryLog(NUMBER_OF_VERTICES)];
        for(int i = 0; i < binaryLog(NUMBER_OF_VERTICES); i++){
            if(i<bitsAsCharArray.length){
                result[i] = bitsAsCharArray[bitsAsCharArray.length - i - 1] == '1';
            }
            else {
                result[i] = false;
            }
        }

        return result;
    }


    private int getFirstDifferentPosition(boolean[] x, boolean[] y){
        int p = -1;
        int length = x.length;
        for(int i = 0; i < length; i++){
            if(x[i] != y[i]){
                p = i;
                break;
            }
        }

        return p;
    }


    private int booleanToInt(Boolean value){
        return value ? 1 : 0;
    }

    protected int posDiff(boolean[] x, boolean[] y){
        int p = getFirstDifferentPosition(x, y);
        return 2*p + booleanToInt(x[p]);
    }


    protected int reducePalette(int k, int x){
        return reducePalette(k, x, 0);
    }

    protected int reducePalette(int k, int x, int index){
        if (x==k){
            ArrayList<Integer> X = new ArrayList<>();
            List<Message> messages = this.getMailbox();
            for(Message message : messages){
                X.add(((CustomMessageContent)message.getContent()).getValue());
            }

            x = firstFree(X);
        }

        sendAll(new Message(new CustomMessageContent(index, x)));

        return x;
    }

    protected int firstFree(ArrayList<Integer> x) {
        int firstFree = 0;
        Iterator<Integer> itr = x.iterator();

        while (itr.hasNext()){
            if (itr.next() == firstFree){
                itr = x.iterator();
                firstFree += 1;
            }
        }

        return firstFree;
    }


    protected int getNodeMessageContent(Node nodeToFilter, int messageIndex, List<Message> messages){
        Optional<Message> message = messages.stream().filter(
                m->m.getSender().compareTo(nodeToFilter) == 0 &&
                ((CustomMessageContent)m.getContent()).getIndex() == messageIndex).findFirst();
        return message.map(value -> ((CustomMessageContent) value.getContent()).getValue()).orElse(-1);
    }
    //endregion

    //region Others
    protected void onFinished(){
        System.out.println("Finished coloring node " + getID() + " in " + nbOfRounds + " rounds.");
        NODES_COLOR.add(getID(), finalColor);
        NODES_FINISHED.set(getID(), true);
        if(NODES_FINISHED.stream().allMatch(Boolean::booleanValue)){
            System.out.println("Algorithm finished in " + nbOfRounds + " rounds.");
            int nbOfColorsUsed = NODES_COLOR.stream().max(Integer::compare).orElse(-1) + 1;
            System.out.println("Used " +  + nbOfColorsUsed +
                    " colors to color " + NUMBER_OF_VERTICES + " vertices with max degree " + MAX_DEGREE + ".");
            ((CustomAbstractTopology)getTopology()).verifyColoration();
        }
    }
    //endregion
}