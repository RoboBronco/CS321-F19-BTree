

public class GeneBankCreateBTree {

    public int cacheSize;
    public int debugLevel;

    public GeneBankCreateBTree () {

    }

    public static void useage() {
        System.out.println("java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
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

    public static void main (String[] args) {
        GeneBankCreateBTree bTree = new GeneBankCreateBTree();
        if (args.length == 4) {
            if (args[0].equals("0")) {

            } else {
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