package customCode.utils;

public class CustomMessageContent {
    private int index;
    private int value;

    public CustomMessageContent(int index, int value){
        this.index = index;
        this.value = value;
    }

    public int getIndex(){
        return index;
    }

    public int getValue() {
        return value;
    }
}
