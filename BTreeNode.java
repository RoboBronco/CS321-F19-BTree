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
        objects = new TreeObject[degree*2 - 1];
    }

    public BTreeNode(int address, int degree, RandomAccessFile file){ // Not sure how to get this to return a BTreeNode...
        try{
            RandomAccessFile raf = file;
            raf.seek(address);

            leafNode = raf.readBoolean();
            metaDataSize = raf.readInt();
            nodeSize = raf.readInt();
            numObjects = raf.readInt();
            locInFile = raf.readInt();
            parentNode = raf.readInt();
            nodeDegree = raf.readInt();
            children = new int[degree*2];
            objects = new TreeObject[degree*2 - 1];
            if(leafNode){
                for (int i=0; i<numObjects+1; i++){
                    children[i] = raf.readInt();
                }
            }
            raf.seek(locInFile + metaDataSize + ((2*nodeDegree)*4));
            for (int j=0; j<numObjects; j++){
                objects[j].setData(raf.readLong());
                objects[j].setFrequency(raf.readInt());
                objects[j].setSequenceLength(raf.readInt());
            }
            // raf.close();
        } catch(IOException e) {
            System.out.println("Error reading from RandomAccessFile. " + e);
        }
    }

    public void insertObject(TreeObject object, int index){ // this might be done in BTree class...
        if (objects[index] == null){
            // numObjects ++;
            objects[index] = object;
        } else if (objects[index].equals(object)){
            objects[index].incrementFrequency();
        }
    }

    public TreeObject relocateObject(int index){
        TreeObject relocateObj =  objects[index];
        objects[index] = null;
        numObjects --;
        return relocateObj;
    }

    public int relocateChild(int index){
        int relocateChild = children[index];
        children[index] = 0;
        return relocateChild;
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

            raf.writeBoolean(leafNode);
            raf.writeInt(metaDataSize);
            raf.writeInt(nodeSize);
            raf.writeInt(numObjects);
            raf.writeInt(locInFile);
            raf.writeInt(parentNode);
            raf.writeInt(nodeDegree);
            if (leafNode){
                for (int i=0; i<numObjects+1; i++){
                    raf.writeInt(children[i]);
                    // if (children[i] != null){
                    //     raf.writeInt(children[i]);
                    // }
                }
            }
            raf.seek(locInFile + metaDataSize + ((2*nodeDegree)*4));
            for (int j=0; j<numObjects; j++){
                System.out.println("int: " + j + " = " + objects[j].toStringACGT());
                raf.writeLong(objects[j].getData());
                raf.writeInt(objects[j].getFrequency());
                raf.writeInt(objects[j].getSequenceLength());
                // if (objects[j] != null){
                //     raf.writeLong(objects[j].getData());
                //     raf.writeInt(objects[j].getFrequency());
                //     raf.writeInt(objects[j].getSequenceLength());
                // }
            }
            // raf.close();
        }catch(IOException e){
            System.out.println("Error writing to RandomAccessFile. " + e);
        }
    }
}