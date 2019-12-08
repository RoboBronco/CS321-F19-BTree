
import java.util.*;

public class BTreeCache{
    private LinkedList<BTreeNode> cacheList;
    private int cacheMaxLength;
    private int cacheLength;
    private int itemIndex;
    private int cacheHits;
    private int cacheSearches;
    private BTree bTree;
    private BTreeNode currentCacheNode;
    private boolean foundIt;

    public BTreeCache(int cacheSize){
        cacheList = new LinkedList<BTreeNode>();
        cacheMaxLength = cacheSize;
        cacheLength = 0;
        cacheSearches = 0;
    }

    public boolean searchItem(TreeObject item){ // Searches all nodes in cache for item. If found it moves that node to the top
        foundIt = false;
        cacheSearches ++;
        for (int i=0; i<cacheLength; i++){
            currentCacheNode = cacheList.get(i);
            if( currentCacheNode.containsObject(item) ){
                cacheHits ++;
                if (i > 0){
                    moveToTop(currentCacheNode);
                }
                foundIt = true;
                return foundIt;                
            }
        }
        return foundIt;
    }

    public int searchItemIndex(TreeObject item){ // Searches all nodes in cache for item. If found it moves that node to the top
        int indexValue = -1;
        cacheSearches ++;
        for (int i=0; i<cacheLength; i++){
            currentCacheNode = cacheList.get(i);
            indexValue = currentCacheNode.containsObjectAtIndex(item);
            if( indexValue != -1 ){
                cacheHits ++;
                if (i > 0){
                    moveToTop(currentCacheNode);
                }
                return indexValue;                
            }
        }
        return indexValue;
    }

    public BTreeNode getNode(int index){    // Returns the node at the specified index in the cache
        return cacheList.get(index);
    }

    public void moveToTop(BTreeNode moveItem){  // Moves node to the top of the cache
        itemIndex = cacheList.indexOf(moveItem);
        cacheList.remove(itemIndex);
        cacheList.addFirst(moveItem);
    }

    public void add(BTreeNode addItem){ // Adds addItem to the cache
        if (cacheLength < cacheMaxLength){
            cacheList.addFirst(addItem);
            cacheLength ++;
        } else {
            cacheList.removeLast();
            cacheList.addFirst(addItem);
        }
    }

    public BTreeNode removeFirstNode(){ // Removes first item in cache and returns that item
        cacheLength --;
        return cacheList.removeFirst();
    }

    public void setBTree(BTree cacheBTree){ // Set the BTree to be used with this cache
		bTree = cacheBTree;
    }

    public int getSearchHits(){ // Returns search hits
        return cacheHits;
    }

    public int getSearches(){   // Returns total number of cache searches
        return cacheSearches;
    }
}