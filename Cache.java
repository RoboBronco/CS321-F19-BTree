import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Object;


/**
 * 
 * Class to create a Cache for BTree implementation
 *
 */
public class Cache {
	static LinkedList<BTreeNode> list;
	static int cap;
	static int numNodes;
	static BTree bTree;
	
    public static void cache(int size){
    	list = new LinkedList<>();
    	cap = size;
    	numNodes = 0;
    }
    
    public static boolean check(TreeObject obj) {
    	boolean isFound = false;
    	BTreeNode node;
    	for(int i = 0; i < numNodes; i++) {
    		node = list.get(i);
    		for(int j = 0; j < node.numObjects; j++) {
    			if(node.objects[j].equals(obj)) {
    				isFound = true;
    			}
    		}
    		
    	}
    	return isFound;
    }
    
    //To implement in BTree, when used, if returned node != give node, add to tree.
    public static BTreeNode add(BTreeNode node) {
    	list.add(node);
    	if(list.size() <= cap) {
    		numNodes++;
    		return node;
    	}else {
    		return list.removeLast();
    	}
    }
    
    public static BTreeNode remove() {
		return list.removeLast();
		// I think this is supposed to insert the node into the BTree when it is removed from the cache...
		// maybe like this... bTree.updateNode(list.removeLast());
    }
	
	public void setBTree(BTree cacheBTree){
		bTree = cacheBTree;
	}
}
