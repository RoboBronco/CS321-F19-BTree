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
            splitChild(s,0,r);
            insertNonFull(s,k);
        } else {
            insertNonFull(r,k);
        }
    }

    public void splitChild(BTreeNode x, int i, BTreeNode y){
        BTreeNode z = new BTreeNode(nextNodeAddress, degree);
        nextNodeAddress += nodeSize;
        // System.out.println("*Splitting_BEFORE*  ->  x.numObjects() = " + x.numObjects());
        // System.out.println("*Splitting_BEFORE**  ->  x.children[0,1,2,3] " + x.children[0] +", "+ x.children[1]+", "+x.children[2]+", "+x.children[3]);
        // System.out.println("*Splitting_BEFORE*  ->  y.numObjects() = " + y.numObjects());
        // System.out.println("*Splitting_BEFORE**  ->  y.children[0,1,2,3] " + y.children[0] +", "+ y.children[1]+", "+y.children[2]+", "+y.children[3]);
        // System.out.println("*Splitting_BEFORE*  ->  z.numObjects() = " + z.numObjects());
        z.setLeaf(y.isLeaf());
        z.setParent(y.getParent());
        z.setNumObjects(degree-1);
        for (int h = 0; h < degree-1; h++){
            z.insertObject(y.relocateObject(h+degree), h);
        }
        // System.out.println("*Splitting**  ->  z.children[0,1,2,3] BEFORE " + z.children[0] +", "+ z.children[1]+", "+z.children[2]+", "+z.children[3]);
        if(!y.isLeaf()){
            for(int j = 0; j < degree; j++){
                z.children[j] = y.relocateChild(j+degree);
            }
            // System.out.println("*Splitting**  ->  y.children[0,1,2,3] " + y.children[0] +", "+ y.children[1]+", "+y.children[2]+", "+y.children[3]);
            // System.out.println("*Splitting**  ->  z.children[0,1,2,3] AFTER " + z.children[0] +", "+ z.children[1]+", "+z.children[2]+", "+z.children[3]);
        }
        y.setNumObjects(degree-1);
        for(int k = x.numObjects() ; k > i ; k--){ 
            x.children[k+1] = x.children[k];
            // System.out.println("*Splitting***  ->  int k = " + k);
        }
        x.setLeaf(false);
        x.children[i+1] = z.nodeAddress();
        // System.out.println("*Splitting***  ->  x.children[0,1,2,3] " + x.children[0] +", "+ x.children[1]+", "+x.children[2]+", "+x.children[3]);
        // System.out.println("*Splitting***  ->  int i = " + i);
        // System.out.println("*Splitting***  ->  x.numObjects() " + x.numObjects());
        for(int m = x.numObjects()-1; m >= i; m--){
            x.objects[m+1]=x.objects[m];
        }
        x.objects[i] = y.relocateObject(degree - 1);
        x.incrementNumObjects();

        // System.out.println("*Splitting_AFTER*  ->  x.numObjects() = " + x.numObjects() + "    x.objects[0]: " + x.objects[0].getData());
        // System.out.println("*Splitting_AFTER**  ->  x.children[0,1,2,3] " + x.children[0] +", "+ x.children[1]+", "+x.children[2]+", "+x.children[3]);
        // System.out.println("*Splitting_AFTER*  ->  y.numObjects() = " + y.numObjects() + "    y.objects[0]: " + y.objects[0].getData());
        // System.out.println("*Splitting_AFTER**  ->  y.children[0,1,2,3] " + y.children[0] +", "+ y.children[1]+", "+y.children[2]+", "+y.children[3]);
        // System.out.println("*Splitting_AFTER*  ->  z.numObjects() = " + z.numObjects() + "    z.objects[0]: " + z.objects[0].getData());
        // System.out.println("*Splitting_AFTER**  ->  z.children[0,1,2,3] " + z.children[0] +", "+ z.children[1]+", "+z.children[2]+", "+z.children[3]);

        // System.out.println("y numObjects() = " + y.numObjects());
        DiskWrite(y);
        // System.out.println("z numObjects() = " + z.numObjects());
        DiskWrite(z);
        // System.out.println("x.numObjects() = " + x.numObjects());
        DiskWrite(x);
    }

    public void insertNonFull(BTreeNode x, TreeObject k){
        int i = x.numObjects() - 1;
        // System.out.println("_insertNonFull_ x.numObjects() = " + x.numObjects());
        if (x.numObjects() >= 0){
            // System.out.print("_insertNonFull_ x.objects[] = ");
            // for(int d=0; d<x.numObjects(); d++){
            //     System.out.print(x.objects[d].getData()+", ");
            // }
            // System.out.println(" ");
        }
        // System.out.print("_insertNonFull_ x.isLeaf() = " + x.isLeaf()+ "   ");
        // System.out.println("-->  x.children[0,1,2,3] " + x.children[0] +", "+ x.children[1]+", "+x.children[2]+", "+x.children[3]);
        if(x.isLeaf()){
            // System.out.println("k.getData = "+k.getData());
            while( i>=0 && k.getData()<x.objects[i].getData()){
                x.objects[i+1] = x.objects[i];
                i --;
            }
            // System.out.println("_insertNonFull_ Data: " + k.getData() + "   int i+1 = " + (i+1));
            x.insertObject(k,(i+1));
            x.incrementNumObjects();
            DiskWrite(x);
        } else {
            while( i>=0 && k.getData()<x.objects[i].getData()){
                // System.out.println("k_data: "+k.getData()+"  compareTo  x.objects[i]_data: " + x.objects[i].getData());
                i --;
                
            }
            i ++;
            // System.out.println("_insertNonFull_ int i = " + i);

            if(x.children[0] == 0){
                return;
            }
            // System.out.println("_insertNonFull_ x.children[i]:" + x.children[i]);
            BTreeNode childNode = new BTreeNode(x.children[i], degree, raf); // reads node - a child node of x
            if( childNode.numObjects() == (2*degree)-1){
                splitChild(x,i,childNode);
                if( k.getData()>x.objects[i].getData()){
                    // System.out.println("2nd k_data: "+k.getData()+"  compareTo  x.objects[i]_data: " + x.objects[i].getData());
                    i ++;
                    childNode = new BTreeNode(x.children[i], degree, raf);
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

    public BTreeNode root(){
        return root;
    }

    public void printTree(BTreeNode printNode){
        int i = 0;
        while (i < printNode.numObjects()){
            // System.out.println("address: " +printNode.nodeAddress() + "   children[] " + printNode.children[0] + ", "+ printNode.children[1] + ", "+ printNode.children[2] + ", "+ printNode.children[3]);
            if (printNode.children[i] > 0){
                BTreeNode child1 = new BTreeNode(printNode.children[i], degree, raf);
                printTree(child1);
            }
            // System.out.println(printNode.objects[i].getData());
            System.out.println(printNode.objects[i].toStringACGT());
            i ++;
        }
        if (printNode.children[i] > 0){
            BTreeNode child2 = new BTreeNode(printNode.children[printNode.numObjects()], degree, raf);
            printTree(child2);
        }
    }
}