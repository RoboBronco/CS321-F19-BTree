public class BTreeNode{

    private Boolean leafNode;
    private int numObjects;
    private int locInFile;
    private int parentNode;
    private int[] children;
    private Long[] objects;

    public BTreeNode(int address, int degree){
        leafNode = true;
        numObjects = 0;
        locInFile = address;
        children = new int[degree+1];
        objects = new Long[degree];
    }

    public void insertObject(long object){
        // numObjects ++;
        // if(objects[0] == null){
        //     objects[0] = object;
        // } else {
        //     int i=0;
        //     while(i<=numObjects){
        //         if(objects[i].equals(object)){
        //             objects[i].incrementFrequency();
        //             break;
        //         } else if (objects[i].getData < object)
        //     }
        // }
        // need to figure out how to order objects and int-pointers... array?
    }

    public Boolean isLeaf(){
        return leafNode;
    }

    public void setParent(int parentLocation){
        parentNode = parentLocation;
    }

    public int numObjects(){
        return numObjects;
    }

    
}