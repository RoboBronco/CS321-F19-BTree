public class BTreeNode{

    private Boolean leafNode;
    // private int nodeSize;
    private int numObjects;
    // private int numChildren;
    private int locInFile; //byte offset in file
    private int parentNode; // byte offset in file
    private int[] children; // each is a byte offset in file
    private Long[] objects; // keys/objects stored in each node

    // When do we set the node size and keep track of where new nodes go?
    // Basically MetaData + Objects + Keys = Node Size (in bytes)... but when is that calculated? When a node is created?

    public BTreeNode(int address, int degree){
        // nodeSize = metaDataSize + ((2*degree -1)*8) + ((2*degree)*4);
        leafNode = false;
        numObjects = 0;
        // numChildren = 0;
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