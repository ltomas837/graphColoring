package customCode;

import customCode.abstractClasses.CustomAbstractNode;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;
import customCode.utils.CustomMessageContent;

import java.util.List;

public class CustomCycleNode extends CustomAbstractNode {
    private int l;
    private Node succ;
    private Node pred;
    private int k;
    private boolean colored;
    private boolean finallyColored;

    //region Getters
    public Node getPred() {
        return pred;
    }

    public Node getSucc() {
        return succ;
    }
    //endregion

    //region Setters
    public void setPred(Node pred) {
        this.pred = pred;
    }

    public void setSucc(Node succ) {
        this.succ = succ;
    }
    //endregion

    //region Overrides
    @Override
    public void onStart() {
        super.onStart();
        l = binaryLog(NUMBER_OF_VERTICES);
        colored = false;
        finallyColored = false;
        send(succ, new Message(new CustomMessageContent(0, finalColor)));
        send(pred, new Message(new CustomMessageContent(0, finalColor)));
    }

    @Override
    public void onClock() {
        if (!colored) {
            finalColor = colorRing(pred, succ, finalColor);
            int lTmp = l;
            l = 1 + binaryLog(1 + binaryLog(l));
            if (lTmp == l) {
                colored = true;
                k = 3;
            }
        }
        else if (!finallyColored) {
            finalColor = reducePalette(k, finalColor);
            k += 1;
            if (k == 6){
                finallyColored = true;
                onFinished();
            }
        }
    }
    //endregion

    //region Distributed Algorithm
    private int colorRing(Node predecessor, Node successor, int x){
        List<Message> messages = this.getMailbox();
        int y = getNodeMessageContent(successor, 0, messages);
        int z = getNodeMessageContent(predecessor, 0, messages);

        x = posDiff(intToBinary(posDiff(intToBinary(z), intToBinary(x))), intToBinary(posDiff(intToBinary(x), intToBinary(y))));

        send(successor, new Message(new CustomMessageContent(0, x)));
        send(predecessor, new Message(new CustomMessageContent(0, x)));

        return x;
    }
    //endregion
}