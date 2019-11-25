public class BTreeNode{

    private Boolean leafNode;
    private int numObjects;
    private int numChildren;
    private int locInFile;
    private int parentNode;
    private int[] children;
    private Long[] objects;

    public BTreeNode(int address, int degree){
        leafNode = false;
        numObjects = 0;
        numChildren = 0;
        locInFile = address;
        children = new int[degree+1];
        objects = new Long[degree];
    }

    public void insertObject(long object, int index){ // this might be done in BTree class...
        numObjects ++;
        objects[index] = object;
    }

    public Boolean isLeaf(){
        return leafNode;
        // return if(children[0] == null);
    }

    public void setLeaf(boolean leafStatus){
        leafNode = leafStatus;
    }

    public void setParent(int parentLocation){
        parentNode = parentLocation;
    }

    public void incrementNumObjects(){
        numObjects ++;
    }

    public int numObjects(){
        return numObjects;
    }

    public int nodeAddress(){
        return locInFile;
    }


    // need disk write method but not sure if it should be in BTree.java or int BTreeNode.java
}