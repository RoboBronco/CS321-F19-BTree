import java.io.IOException;
import java.io.RandomAccessFile;

public class BTreeNode{

    private Boolean leafNode;
    private int metaDataSize; // 
    private int nodeSize;
    private int numObjects;
    private int locInFile; //byte offset in file
    private int parentNode; // byte offset in file
    private int nodeDegree;
    public int[] children; // each is address in file of child nodes
    public TreeObject[] objects; // keys/objects stored in each node

    // When do we set the node size and keep track of where new nodes go?
    // Basically MetaData + Objects + Keys = Node Size (in bytes)... but when is that calculated? When a node is created?

    public BTreeNode(int address, int degree){
        leafNode = true;
        metaDataSize = 1 + 4 + 4 + 4 + 4 + 4;
        nodeSize = metaDataSize + ((2*degree -1)*(8+4+4)) + ((2*degree)*4); 
        numObjects = 0;
        locInFile = address;
        parentNode = 0;
        nodeDegree = degree;
        children = new int[degree*2];
        objects = new TreeObject[(degree*2) - 1];
    }

    public BTreeNode(int address, int degree, RandomAccessFile file){ // Not sure how to get this to return a BTreeNode...
        try{
            RandomAccessFile raf = file;
            raf.seek(address);
            System.out.print("address:"+address);

            leafNode = raf.readBoolean();
            System.out.print(" -- leafNode:"+leafNode);
            metaDataSize = raf.readInt();
            System.out.print(" -- metaDataSize:"+metaDataSize);
            nodeSize = raf.readInt();
            System.out.print(" -- nodeSize:"+nodeSize);
            numObjects = raf.readInt();
            System.out.println(" -- numObjects:"+numObjects);
            locInFile = raf.readInt();
            System.out.print("locInFile:"+locInFile);
            parentNode = raf.readInt();
            System.out.print(" -- parentNode:"+parentNode);
            nodeDegree = raf.readInt();
            System.out.println(" -- nodeDegree:"+nodeDegree);
            children = new int[degree*2];
            objects = new TreeObject[degree*2 - 1];

            System.out.println("readFromFile -- children[] ");
            for (int i=0; i<children.length; i++){
                System.out.print("ReadingFilePointer = " + raf.getFilePointer() + " ");
                children[i] = raf.readInt();
                System.out.println(children[i] + " ");
            }
            System.out.println(" ");

            raf.seek(locInFile + metaDataSize + ((2*nodeDegree)*4));
            // System.out.println("BTreeNode_RAF\t numObjects = " + numObjects);
            for (int j=0; j<numObjects; j++){
                Long reloadData = raf.readLong();
                int reloadFrequency = raf.readInt();
                int reloadSedLen = raf.readInt();
                TreeObject reloadObj = new TreeObject(reloadData, reloadSedLen);
                reloadObj.setFrequency(reloadFrequency);
                objects[j] = reloadObj;
            }
        } catch(IOException e) {
            System.out.println("Error reading from RandomAccessFile. " + e);
        }
    }

    public void insertObject(TreeObject object, int index){ // this might be done in BTree class...
        if (objects[index] == null){
            objects[index] = object;
        }else if (objects[index].equals(object)){
            objects[index].incrementFrequency();
        } else {
            objects[index] = object;
        }
    }

    public TreeObject relocateObject(int index){
        TreeObject relocateObj =  objects[index];
        objects[index] = null;
        return relocateObj;
    }

    public int relocateChild(int index){
        int relocateChild = children[index];
        children[index] = -1;
        return relocateChild;
    }

    public Boolean isLeaf(){
        return leafNode;
        // if (children[0] <= 0){
        //     return true;
        // } else {
        //     return false;
        // }
    }

    public void setLeaf(boolean leafStatus){
        leafNode = leafStatus;
    }

    public void setParent(int parentLocation){
        parentNode = parentLocation;
    }

    public int getParent(){
        return parentNode;
    }

    public void incrementNumObjects(){
        numObjects ++;
    }

    public void decrementNumObjects(){
        numObjects --;
    }

    public void setNumObjects(int newValue){
        numObjects = newValue;
    }

    public int numObjects(){
        return numObjects;
    }

    public int nodeAddress(){
        return locInFile;
    }

    public int nodeSize(){
        return nodeSize;
    }

    public void writeToFile(RandomAccessFile file){
        try{
            RandomAccessFile raf = file;
            raf.seek(locInFile);
            System.out.println("WriteToFile....Address:"+locInFile);

            raf.writeBoolean(leafNode);
            raf.writeInt(metaDataSize);
            raf.writeInt(nodeSize);
            raf.writeInt(numObjects);
            raf.writeInt(locInFile);
            raf.writeInt(parentNode);
            raf.writeInt(nodeDegree);
            
            System.out.println("children.length: " + children.length);
            
            for (int i=0; i<children.length; i++){
                System.out.println("WritingFilePointer = " + raf.getFilePointer());
                raf.writeInt(children[i]);
                System.out.println("writeInt(children[i]) " + children[i]);
            }
            
            raf.seek(locInFile + metaDataSize + ((2*nodeDegree)*4));
            raf.seek(locInFile + metaDataSize + ((2*nodeDegree)*4));
            for (int j=0; j<numObjects; j++){
                System.out.println("int: " + j + " = " + objects[j].getData());
                raf.writeLong(objects[j].getData());
                raf.writeInt(objects[j].getFrequency());
                raf.writeInt(objects[j].getSequenceLength());
            }
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            
        }catch(IOException e){
            System.out.println("Error writing to RandomAccessFile. " + e);
        }
    }

    
}