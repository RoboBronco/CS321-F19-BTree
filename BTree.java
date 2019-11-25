import java.io.RandomAccessFile;

public class BTree{
    private int root;
    private int degree;
    private int seqLength;
    private RandomAccessFile raf;

    public BTree(String fileName, int sequenceLength, int degreeT){
        degree = degreeT;
        seqLength = sequenceLength;
        String filePath = fileName + ".btree.data." + seqLength + "." + degree;
        raf = new RandomAccessFile(filePath, "rw");
    }

    public void insert(BTree t, TreeObject k){
        BTreeNode r = BTreeNode.getNode(root);
        if (r.numObjects() == (2*degree)-1){
            BTreeNode s = new BTreeNode();
            t.root = s;
            s.setLeaf(false);
            s.cildren[0] = r;
            splitChild(s,1,r);
            insertNonFull(s,k);
        } else {
            insertNonFull(r,k);
        }
    }

    public void splitChild(BTreeNode x, int i, BTreeNode y){
        BTreeNode z = new BTreeNode(degree);
        z.setLeaf(y.isLeaf());
        // z.count = degree-1
        for (int i = 0; i < degree-1; i++){
            z.insertObject(y.objects[i+degree], i);
        }
        if(!y.isLeaf()){
            for(int j = 0; j < degree; j++){
                z.children[j] = y.children[j+degree];
            }
        }
        for(int k = x.numObjects() ; k>i ; k--){ // pay attention to index locations here, may be off by 1
            x.children[k+1] = x.children[k];
        }
        x.children[i+1] = z;
        for(int l = x.numObjects(); l > i; i--){
            x.objects[l+1]=x.objects[l];
        }
        x.objects[i] = y.objects[degree];
        x.incrementNumObjects();
        y.diskWrite();
        z.diskWrite();
        x.diskWrite();
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
            x.diskWrite();
        } else {
            while( i>=1 && k.getData()<x.objects[i].getData()){
                i --;
            }
            i ++;
            BTreeNode childNode = DiskRead(x.children[i]); // reads node - a child node of x
            if( childNode.numObjects() == (2*degree)+1){
                splitChild(x,i,childNode);
                if( k.getData()>x.objects[i].getData()){
                    i ++;
                }
            }
            insertNonFull(childNode, k);
        }
    }

    public Boolean search(BTreeNode x, BTreeObject k){
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
            BTreeNode childNode = DiskRead(x.children[i]);
            return search(childNode,k);
        }
    }

    private BTreeNode DiskRead(int nodePointer){

    }
}