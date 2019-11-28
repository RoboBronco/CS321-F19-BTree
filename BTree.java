import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BTree{
    private BTreeNode root;
    private int degree;
    private int seqLength;
    private int nextNodeAddress;
    private int nodeSize;
    private RandomAccessFile raf;

    public BTree(String fileName, int sequenceLength, int degreeT)throws FileNotFoundException{
        degree = degreeT;
        seqLength = sequenceLength;
        String filePath = fileName + ".btree.data." + seqLength + "." + degree;
        raf = new RandomAccessFile(filePath, "rw");
        nextNodeAddress = 56; // Temporary value that is intended to be the MetaData size of the BTree
        root = new BTreeNode(nextNodeAddress, degree);
        nodeSize = root.nodeSize();
        nextNodeAddress += nodeSize;
    }

    public void insert(TreeObject k){
        BTreeNode r = root;
        if (r.numObjects() == (2*degree)-1){
            BTreeNode s = new BTreeNode(nextNodeAddress, degree);
            nextNodeAddress += nodeSize;
            root = s;
            s.setLeaf(false);
            s.children[0] = r.nodeAddress();
            r.setParent(s.nodeAddress());
            splitChild(s,1,r);
            insertNonFull(s,k);
        } else {
            insertNonFull(r,k);
        }
    }

    public void splitChild(BTreeNode x, int i, BTreeNode y){
        BTreeNode z = new BTreeNode(nextNodeAddress, degree);
        nextNodeAddress += nodeSize;
        z.setLeaf(y.isLeaf());
        z.setNumObjects(degree-1);
        for (int h = 0; h < degree-1; h++){
            z.insertObject(y.relocateObject(h+degree), h);
        }
        if(!y.isLeaf()){
            for(int j = 0; j < degree; j++){
                z.children[j] = y.relocateChild(j+degree);
            }
        }
        y.setNumObjects(degree-1);
        for(int k = x.numObjects() ; k>i ; k--){ 
            x.children[k+1] = x.children[k];
        }
        x.children[i] = z.nodeAddress();
        for(int m = x.numObjects(); m > i; m--){
            x.objects[m+1]=x.objects[m];
        }
        x.objects[i] = y.relocateObject(degree);
        x.incrementNumObjects();
        System.out.println("y numObjects() = " + y.numObjects());
        DiskWrite(y);
        System.out.println("z numObjects() = " + z.numObjects());
        DiskWrite(z);
        System.out.println("x.numObjects() = " + x.numObjects());
        DiskWrite(x);
    }

    public void insertNonFull(BTreeNode x, TreeObject k){
        int i = x.numObjects();
        System.out.println("x.numObjects() = " + i);
        if(x.isLeaf()){
            while( i>=1 && k.getData()<x.objects[i-1].getData()){
                x.objects[i] = x.objects[i-1];
                i --;
            }
            x.objects[i] = k;
            x.incrementNumObjects();
            DiskWrite(x);
        } else {
            while( i>=1 && k.getData()<x.objects[i-1].getData()){
                i --;
            }
            i ++;
            BTreeNode childNode = new BTreeNode(x.children[i], degree, raf); // reads node - a child node of x
            if( childNode.numObjects() == (2*degree)+1){
                splitChild(x,i,childNode);
                if( k.getData()>x.objects[i].getData()){
                    i ++;
                }
            }
            insertNonFull(childNode, k);
        }
    }

    public Boolean search(BTreeNode x, TreeObject k){
        int i = 1;
        while( i<=x.numObjects() && k.getData()>x.objects[i].getData()){
            i ++;
        }
        if( i<=x.numObjects() && k.equals(x.objects[i])){
            return true;
        }
        if( x.isLeaf()){
            System.out.println("Data not found");
            return false;
        } else {
            BTreeNode childNode = new BTreeNode(x.children[i], degree, raf);
            return search(childNode,k);
        }
    }

    private void DiskWrite(BTreeNode writeNode){
        writeNode.writeToFile(raf);
    }

    public void closeRandomAccessFile(){
        try{
            raf.close();
        } catch(IOException e){
            System.out.println("Issue closing RandomAccessFile. " + e);
        }
    }
    
}