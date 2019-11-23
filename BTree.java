public class BTree{
    private int root;
    private int degree;
    private int k;

    public BTree(int degreeT){
        degree = degreeT;
    }

    public void insert(BTree t, TreeObject treeObj){
        BTreeNode r = BTreeNode.getNode(root); //
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
        // for(int l = x.numObjects(); )
    }
}