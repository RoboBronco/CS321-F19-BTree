import java.io.*;
import java.util.Scanner;

class GeneBankSearch{

    private Boolean withCache;
    private Boolean withDebug;
    private RandomAccessFile bTreeFile;
    private BTree bTree;
    private File queryFile;

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
    private boolean getWithCache(boolean withCache) {
        return this.withCache;
    }

    private boolean getWithDebug(boolean withDebug) {
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
        setBTree(bTreeFile);
    }

    //no cache and debug level 0
    private GeneBankSearch(String cache, String bTreeFile, String queryFile) {
        check(cache, bTreeFile, queryFile);
        System.out.println("GeneBankSearch with no cache and default debug");
    }

    //with cache or with debug level 1
    private GeneBankSearch(String cache, String bTreeFile, String queryFile, String cacheOrDebug) {
        check(cache, bTreeFile, queryFile);
        System.out.println("GeneBankSearch with possible cache or debug level 1");
    }

    //with cache and with debug level 1
    private GeneBankSearch(String cache, String bTreeFile, String queryFile, String cacheSize, String debug) {
        check(cache, bTreeFile, queryFile);
        System.out.println("GeneBankSearch with cache and debug lavel 1");
    }

    public static void useage() {
		System.out.println(
				"java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
		System.exit(-1);
	}
    public static void main(String[] args) throws FileNotFoundException {
        GeneBankSearch search = new GeneBankSearch(args[0], args[1], args[2]);
    }
}