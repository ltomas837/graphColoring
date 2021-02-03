package customCode;

import customCode.abstractClasses.CustomAbstractNode;
import customCode.utils.CustomMessageContent;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CustomNode extends CustomAbstractNode {
    private static List<String> POSSIBLE_COLORS = new ArrayList<>();

    private List<Node> preds;
    private List<Node> succs;
    private List<Integer> colors;
    private List<Integer> k;
    private List<Integer> l;
    private List<Boolean> color6Applied;
    private List<Boolean> doShiftDown;
    private List<Boolean> color6To3Applied;
    private boolean firstFinalReducePaletteToApply;
    private int finalK;
    private boolean finished;
    private boolean init;

    //region Overrides
    @Override
    public void onStart() {
        super.onStart();
        // Compute delta-orientation
        preds = getNeighbors().stream().
                filter(n -> n.getID() < this.getID()).
                collect(Collectors.toList());
        succs = getNeighbors().stream().
                filter(n -> n.getID() > this.getID()).
                collect(Collectors.toList());
        colors = new ArrayList<>();
        k = new ArrayList<>();
        doShiftDown = new ArrayList<>();
        l = new ArrayList<>();
        color6Applied = new ArrayList<>();
        color6To3Applied = new ArrayList<>();
        init = true;
        finished = false;
        firstFinalReducePaletteToApply = false;
    }

    @Override
    public void onClock() {
        if(init){
            for (int i = 0; i < MAX_DEGREE; i++) {
                colors.add(getID());
                color6Applied.add(false);
                color6To3Applied.add(false);
                l.add(binaryLog(NUMBER_OF_VERTICES));
                k.add(-1);
                doShiftDown.add(true);
                sendAll(new Message(new CustomMessageContent(i, getID())));
            }

            init = false;
        }
        else if(color6To3Applied.stream().allMatch(Boolean::booleanValue)){
            if(!finished){
                if(firstFinalReducePaletteToApply){
                    finalColor = reducePalette(finalK, finalColor);
                    finalK += 1;
                    if (finalK == Math.pow(3, MAX_DEGREE)){
                        finished = true;
                        System.out.println("Finished coloring node " + getID() + " in " + nbOfRounds + " rounds.");
                        onFinished();
                    }
                }
                else{
                    finalColor = vectorColorToInt(colors);
                    finalK = MAX_DEGREE+1;
                    firstFinalReducePaletteToApply = true;
                    sendAll(new Message(new CustomMessageContent(0, finalColor)));
                }
            }
        }
        else{
            IntStream.range(0, colors.size()).forEach(index -> {
                Node pred = preds.size() > index ? preds.get(index) : null;
                List<Node> currentSuccs = getNeighbors().stream().filter(n->!n.equals(pred)).collect(Collectors.toList());

                if (!color6Applied.get(index)) {
                    colors.set(index, color6(pred, currentSuccs, colors.get(index),index));
                    int lTmp = l.get(index);
                    l.set(index, 1 + binaryLog(l.get(index)));
                    if (lTmp == l.get(index)) {
                        color6Applied.set(index, true);
                        k.set(index, 3);
                    }
                }
                else if (!color6To3Applied.get(index)) {
                    if(doShiftDown.get(index)){
                        colors.set(index, shiftDown(pred, currentSuccs, colors.get(index), index));
                        doShiftDown.set(index, false);
                    }
                    else{
                        colors.set(index, reducePalette(k.get(index), colors.get(index), index));
                        doShiftDown.set(index, true);
                        k.set(index, k.get(index) + 1);
                        if (k.get(index) == 6){
                            color6To3Applied.set(index, true);
                        }
                    }
                }
            });
        }
    }
    //endregion

    //region Distributed Algorithm
    private int color6(Node predecessor, List<Node> successors, int x, int index){
        int y;
        if(predecessor == null){
            ArrayList<Integer> X = new ArrayList<>();
            X.add(getID());
            y = firstFree(X);
        }
        else {
            List<Message> messages = this.getMailbox();
            y = getNodeMessageContent(predecessor, index, messages);
        }

        x = posDiff(intToBinary(x), intToBinary(y));

        for (Node successor : successors) {
            send(successor, new Message(new CustomMessageContent(index, x)));
        }

        return x;
    }


    private int shiftDown(Node predecessor, List<Node> successors, int x, int index){
        int y;
        if(predecessor != null){
            List<Message> messages = this.getMailbox();
            y = getNodeMessageContent(predecessor, index, messages);
        }
        else {
            ArrayList<Integer> X = new ArrayList<>();
            X.add(x);
            y = firstFree(X);
        }

        x = y;
        for (Node successor : successors) {
            send(successor, new Message(new CustomMessageContent(index, x)));
        }

        return x;
    }


    private int vectorColorToInt(List<Integer> vectorColor){
        String vectorHash = vectorColor.toString();
        if(!POSSIBLE_COLORS.contains(vectorHash)){
            POSSIBLE_COLORS.add(vectorHash);
        }

        return POSSIBLE_COLORS.indexOf(vectorHash);
    }
    //endregion
}