import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

class GeneBankSearch{

    private boolean withCache;
    private boolean withDebug;
    private BTree bTree;
    private File queryFile;
    private int cacheSize;

    //no cache and debug level 0
    private GeneBankSearch(String cache, String bTreeFile, String queryFile) {
        check(cache, bTreeFile, queryFile);
        if (getWithCache()) {
            System.out.println("Cache option selected, but cache size not provided.");
            useage();
        }
        System.out.println("GeneBankSearch with no cache and default debug"); //for testing
    }

    //with cache or with debug level 1
    private GeneBankSearch(String cache, String bTreeFile, String queryFile, String cacheOrDebug) {
        check(cache, bTreeFile, queryFile);
        if (getWithCache())
            checkCacheSize(cacheOrDebug);
        else if (getWithDebug())
            checkDebug(cacheOrDebug);
        else
            useage();
        System.out.println("GeneBankSearch with possible cache or manual level debug"); //for testing
    }

    //with cache and with debug level 1
    private GeneBankSearch(String cache, String bTreeFile, String queryFile, String cacheSize, String debug) {
        check(cache, bTreeFile, queryFile);
        if (!getWithCache()) {
            System.out.println("Cache size provided, but cache option not selected");
            useage();
        }
        checkCacheSize(cacheSize);
        checkDebug(debug);
        System.out.println("GeneBankSearch with cache and manual level debug"); //for testing
    }
    
    //setters
    private void setWithCache(boolean withCache) {
        this.withCache = withCache;
    }

    private void setWithDebug(boolean withDebug) {
        this.withDebug = withDebug;
    }

    private void setQueryFile(File queryFile) {
        this.queryFile = queryFile;
    }

    //getters
    private boolean getWithCache() {
        return this.withCache;
    }

    private boolean getWithDebug() {
        return this.withDebug;
    }

    private BTree getBTree() {
        return this.bTree;
    }

    private File getQueryFile() {
        return this.queryFile;
    }

    //checks for every constructor
    private void check(String cache, String bTreeFile, String queryFile) {
        //check cache
        if(cache.equals("0")) {
            this.withCache = false;
        } else if (cache.equals("1")) {
            this.withCache = true;
        } else {
            System.out.println("Error with (no/with Cache) argument.");
            useage();
        }
        //check bTreeFile
        try {
            this.bTree = new BTree(bTreeFile);
        } catch (Exception e) {
            System.out.println("Error with <bTree file> argument.");
            useage();
        }
        //check queryFile
        try {
            this.queryFile = new File(queryFile);
        } catch (Exception e) {
            System.out.println("Error with <query file> argument.");
            useage();
        }
    }

    public void checkCacheSize(String cacheSize) {
        int size = 0;
        try {
            size = Integer.parseInt(cacheSize);
        } catch (Exception e) {
            System.out.println("Cache size is not an integer.");
            useage();
        }
        if (size < 5) {
            System.out.println("Cache size is too small, must be 5 or greater.");
            useage();
        }
    }

    public void checkDebug(String debug) {
        if (debug.equals("0")) {
            setWithDebug(false);
        } else if (debug.equals("1")) {
            setWithDebug(true);
        } else {
            System.out.println("Debug level must be 0 or 1.");
            useage();
        }
    }

    public static void useage() {
		System.out.println(
				"java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
		System.exit(-1);
	}

    private static long stringToLong(String objectString) {
		long objValue = 0;
		String binaryString = "";
		for (int i = 0; i < objectString.length(); i++) {
			if (objectString.charAt(i) == 'a' || objectString.charAt(i) == 'A') {
				binaryString += "00";
			} else if (objectString.charAt(i) == 'c' || objectString.charAt(i) == 'C') {
				binaryString += "01";
			} else if (objectString.charAt(i) == 'g' || objectString.charAt(i) == 'G') {
				binaryString += "10";
			} else if (objectString.charAt(i) == 't' || objectString.charAt(i) == 'T') {
				binaryString += "11";
			}
		}
		objValue = Long.parseLong(binaryString, 2);
		return objValue;
	}

    public static void main(String[] args) throws FileNotFoundException {
        GeneBankSearch search = null;
        if (args.length == 3) {
            search = new GeneBankSearch(args[0], args[1], args[2]);
        } else if (args.length == 4) {
            search = new GeneBankSearch(args[0], args[1], args[2], args[3]);
        } else if (args.length == 5) {
            search = new GeneBankSearch(args[0], args[1], args[2], args[3], args[4]);
        } else {
            useage();
        }

        Boolean useCache = false;
        if (args[0].equals("1")){
            useCache = true;
        }
        int cacheSize = 0;

        // parse throught the query file and generate output
        BTree searchingBTree = search.getBTree();
        File queryFile = search.getQueryFile();
        int sequenceLength = searchingBTree.getSequenceLength();
        ArrayList<TreeObject> searchedObjects = new ArrayList<TreeObject>();
        BTreeCache treeCache = null;

        // build the cache if args call for a cache
        searchingBTree.setCacheBool(false);
        if(useCache){
            searchingBTree.setCacheBool(true);
            cacheSize = Integer.parseInt(args[3]);
            treeCache = new BTreeCache(cacheSize);
            treeCache.setBTree(searchingBTree);
            searchingBTree.setCache(treeCache);
        }

        // This setup doesn't keep track of duplicate search items...
        Scanner queryScanner = new Scanner(queryFile);
        while(queryScanner.hasNextLine()){
            String queryString = queryScanner.nextLine();
            Long queryValue = stringToLong(queryString);
            TreeObject searchObject = new TreeObject(queryValue, sequenceLength);
            if (searchedObjects.length != 0){
                int checkIndex = 0;
                while (checkIndex < searchedObjects.length){
                    if (searchedObjects.get(checkIndex).getData() == queryValue){
                        return;
                    } else {
                        checkIndex ++;
                    }
                }
            }

            if (useCache){
                if(treeCache.searchItem(searchObject)){
                    int address = treeCache.getNode(1).nodeAddress();
                    BTreeNode searchThisNode = searchingBTree.loadNode(address);
                    TreeObject tempObject = searchingBTree.search(searchThisNode, searchObject);
                    // if (tempObject != null){
                    //     int insertIndex = 0;
                    //     while ( insertIndex<searchedObjects.length && tempObject.getData()>searchedObjects.get(insertIndex).getData()){
                    //         insertIndex ++;
                    //     }
                    //     if (insertIndex < searchedObjects.length){
                    //         searchedObjects.add(insertIndex, tempObject);
                    //     } else {
                    //         searchedObjects.add(tempObject);
                    //     }
                    // }
                } else {
                    TreeObject tempObject = searchingBTree.search(searchingBTree.root(),searchObject);
                }
            } else {
                TreeObject tempObject = searchingBTree.search(searchingBTree.root(),searchObject); // Working on what to return/print/write to file
            }
            if (tempObject != null){
                int insertIndex = 0;
                while ( insertIndex<searchedObjects.length && tempObject.getData()>searchedObjects.get(insertIndex).getData()){
                    insertIndex ++;
                }
                if (insertIndex < searchedObjects.length){
                    searchedObjects.add(insertIndex, tempObject);
                } else {
                    searchedObjects.add(tempObject);
                }
            }   
        }
        for (int p=0; p<searchedObjects.length; p++){
            System.out.println(searchedObjects.get(p).toStringACGT());
        }

        queryScanner.close();
        searchingBTree.closeDownBTree();
    }
}