import java.io.RandomAccessFile;

public class BTree{
    private BTreeNode root;
    private int degree;
    private int seqLength;
    private int nextNodeAddress;
    private int nodeSize;
    private RandomAccessFile raf;

    public BTree(String fileName, int sequenceLength, int degreeT){
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
        // z.count = degree-1
        for (int h = 0; h < degree-1; h++){
            z.insertObject(y.objects[h+degree], h);
        }
        if(!y.isLeaf()){
            for(int j = 0; j < degree; j++){
                z.children[j] = y.children[j+degree];
            }
        }
        for(int k = x.numObjects() ; k>i ; k--){ // pay attention to index locations here, may be off by 1
            x.children[k+1] = x.children[k];
        }
        x.children[i+1] = z.nodeAddress();
        for(int l = x.numObjects(); l > i; i--){
            x.objects[l+1]=x.objects[l];
        }
        x.objects[i] = y.objects[degree];
        x.incrementNumObjects();
        DiskWrite(y);
        DiskWrite(z);
        DiskWrite(x);
        }

    public void insertNonFull(BTreeNode x, TreeObject k){
        int i = x.numObjects();
        if( x.isLeaf()){
            while( i>=1 && k.getData()<x.objects[i-1].getData()){
                x.objects[i] = x.objects[i-1];
                i --;
            }
            x.objects[i] = k;
            x.incrementNumObjects();
            DiskWrite(x);
        } else {
            while( i>=1 && k.getData()<x.objects[i].getData()){
                i --;
            }
            i ++;
            BTreeNode childNode = BTreeNode(raf, x.children[i]); // reads node - a child node of x
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
            BTreeNode childNode = BTreeNode(raf, x.children[i]);
            return search(childNode,k);
        }
    }

    private void DiskWrite(BTreeNode writeNode){
        writeNode.writeToFile(raf);
    }
    
}