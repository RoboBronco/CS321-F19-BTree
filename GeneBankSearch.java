import java.io.*;
import java.util.Scanner;

class GeneBankSearch{

    private boolean withCache;
    private boolean withDebug;
    private RandomAccessFile bTreeFile;
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

    private void setBTree(String bTreeFile) {
        try {
            this.bTree = new BTree(bTreeFile);
        } catch (Exception e) {
            System.out.println("BTree failed to construct.");
            useage();
        }
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
            this.bTreeFile = new RandomAccessFile(bTreeFile, "r");
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
        //create BTree object
        //setBTree(bTreeFile); //not currently working
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
            System.out.println("Cache size is to small, must be 5 or greater.");
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

    public static void main(String[] args) throws FileNotFoundException {
        GeneBankSearch search;
        if (args.length == 3)
            search = new GeneBankSearch(args[0], args[1], args[2]);
        else if (args.length == 4)
            search = new GeneBankSearch(args[0], args[1], args[2], args[3]);
        else if (args.length == 5)
            search = new GeneBankSearch(args[0], args[1], args[2], args[3], args[4]);
        else
            useage();
    }
}