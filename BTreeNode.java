public class BTreeNode{

    private Boolean leafNode;
    private int numObjects;
    private int locInFile;
    private int parentNode;

    public BTreeNode(int address, int parent){
        leafNode = false;
        numObjects = 0;
        locInFile = address;
        parentNode = parent;
    }

    public void insertObject(long object){
        numObjects ++;
        // need to figure out how to order objects and int-pointers... array?
    }
}