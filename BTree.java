public class BTree{
    private int root;
    private int t;
    private int k;

    public BTree(int tVal, int kVal){
        t = tVal;
        k = kVal;
    }

    public insert(TreeObject treeObj){
        BTreeNode r = BTreeNode.getNode(root);
        if (r.numObjects() == (2*t)-1){
            BTreeNode s = new BTreeNode();
            
        }
    }
}