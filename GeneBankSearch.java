import java.util.Scanner;

class GeneBankSearch{

    public static void useage() {
		System.out.println(
				"Usage: java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
		System.exit(-1);
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 3 || args.length > 5){
            useage();
        }

        Boolean withCache = false;
        Boolean withDebug = false;
        if (args[0].equals("1") || args[0].equals("0")){
            if (args[0].equals("1")){
                withCache = true;
            }
        } else {
            System.out.println("Error with (no/with Cache) argument.");
            useage();
        }

        RandomAccessFile bTreeFile = new RandomAccessFile(args[1], "r");
        BTree bTree = new BTree(bTreeFile);

        File queryFile = new File(args[2]);

        if (withCache){
            if (args.length < 4){
                System.out.println("Error reading cache size.");
                useage();
            }
            int cacheSize = Integer.parseInt(args[3]);
            if (cacheSize <= 0){
                System.out.println("cache size is not valid. Enter value > 0.");
                useage();
            }
            if (args.length == 5){
                if (args[4].equals("0")){
                    withDebug = true;
                }
            }
        } else {
            if (args.length == 4){
                if (args[3].equals("0")){
                    withDebug = true;
                }
            }
        }

        Scanner queryScanner = new Scanner(queryFile);
        while (queryScanner.hasNext()){
            String data
        }
        
    }

}