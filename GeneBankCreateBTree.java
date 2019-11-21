import java.util.Scanner;
import java.io.*;

public class GeneBankCreateBTree {

    public int cacheSize;
    public int debugLevel;

    public GeneBankCreateBTree () {

    }

    public static void useage() {
        System.out.println("Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
        System.exit(-1);
    }

    public void setCacheSize(int size) {
        this.cacheSize = size;
    }

    public void setDebugLevel(int level) {
        this.debugLevel = level;
    }

    public int getDebugLevel() {
        return this.debugLevel;
    }

    public int getCacheSize() {
        return this.cacheSize;
    }

    public static void main (String[] args) throws FileNotFoundException {
        GeneBankCreateBTree bTree = new GeneBankCreateBTree();
        System.out.println(args.length);
        System.out.println(args[2]);
        if(args.length < 4 || args.length > 6){
            useage();
        }

        boolean useCache = false; // configure using args[0]
        int degree = Integer.parseInt(args[1]); // set degree from args[1]
        int sequenceLength = Integer.parseInt(args[3]); // set sequence length from args[3]

        // try to parse through the gbk file
        // try{
            File file = new File(args[2]);
            Scanner scanner = new Scanner(file);
            String startPt = "ORIGIN";
            String stopPt = "//";
            Boolean foundStartPt = false;
            Boolean foundStopPt = false;

            while(!foundStartPt){
                if (scanner.hasNextLine()){
                    String fileString = scanner.nextLine();
                    if (fileString.equals(startPt)){
                        foundStartPt = true;
                    }                    
                }
            }
            while(!foundStopPt && foundStartPt){
                if (scanner.hasNextLine()){
                    String dataString = scanner.nextLine();
                    if (dataString.equals(stopPt)){
                        foundStopPt = true;
                        return;
                    }
                    System.out.println(dataString); // initial test to see if parsing is working correctly
                    // need to break data into moving window groups of sequenceLength size
                }
            }
            

        // } catch (Exception e){
        //     useage();
        // }
        


        if (args.length == 4) {
            if (args[0].equals("0")) {
                // no cache
            } else if (args[0].equals("1")){
                // cache -> should be args[4] for cache size
            } else{
                useage();
            }
        } else if (args.length == 5) {
            if (args[0].equals("0")) {
                try {
                    bTree.setDebugLevel(Integer.parseInt(args[4]));
                } catch (Exception e) {
                    useage();
                }
            } else if (args[0].equals("1")) {
                try {
                    bTree.setCacheSize(Integer.parseInt(args[4]));
                } catch (Exception e) {
                    useage();
                }
            } else {
                useage();
            }
        } else if (args.length == 6) {
            if (args[0].equals("1")) {
                try {
                    bTree.setCacheSize(Integer.parseInt(args[4]));
                } catch (Exception e) {
                    useage();
                }
                try {
                    bTree.setDebugLevel(Integer.parseInt(args[5]));
                } catch (Exception e) {
                    useage();
                }
            } else {
                useage();
            }
        } else {
            useage();
        }
    }
}