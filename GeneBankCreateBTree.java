import java.util.Scanner;
import java.io.*;

public class GeneBankCreateBTree {

	public int cacheSize;
	public int debugLevel;

	public GeneBankCreateBTree() {

	}

	public static void useage() {
		System.out.println(
				"Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
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

	private static boolean validSequence(String data, int startIndex, int seqLen) {
		boolean valid = true;
		int maxStartIndex = data.length() - seqLen;
		if (startIndex > maxStartIndex) {
			valid = false;
			return valid;
		}
		for (int i = startIndex; i < startIndex + seqLen; i++) {
			if (data.charAt(i) == 'n') {
				valid = false;
				return valid;
			}
		}
		return valid;
	}

	private static String objectString(String data, int startIndex, int seqLen) {
		String objString = "";
		for (int i = 0; i < seqLen; i++) {
			objString += data.charAt(startIndex + i);
		}
		System.out.println(stringToLong(objString)); // for testing purposes
		return objString;
	}

	private static long stringToLong(String objectString) {
		long objValue = 0;
		String binaryString = "";
		for (int i = 0; i < objectString.length(); i++) {
			if (objectString.charAt(i) == 'a') {
				binaryString += "00";
			} else if (objectString.charAt(i) == 'c') {
				binaryString += "01";
			} else if (objectString.charAt(i) == 'g') {
				binaryString += "10";
			} else if (objectString.charAt(i) == 't') {
				binaryString += "11";
			}
		}
		objValue = Long.parseLong(binaryString, 2);
		return objValue;
	}

	public static void main(String[] args) throws FileNotFoundException {
		GeneBankCreateBTree bTree = new GeneBankCreateBTree();
		int degree = -1;
		String fileName = args[2];
		
		try {
			degree = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println(args[1] + " is not an integer.");
			useage();
		}
		if (degree == 0) {
			//need to create algorith to compute this value, using what was provided in class currently
			degree = 127;
		}
		int sequenceLength = 0;
		try {
			sequenceLength = Integer.parseInt(args[3]);
		} catch (Exception e) {
			System.out.println(args[3] + " is not an integer.");
			useage();
		}
		if (sequenceLength < 1 || sequenceLength > 31) {
			System.out.println("Sequence length must be between 1 and 31 inclusive.");
			useage();
		}

		// parse through the gbk file
		File file = new File(fileName);
		Scanner scanner = new Scanner(file);
		String startPt = "ORIGIN";
		String stopPt = "//";
		String dataString = "";
		Boolean foundStartPt = false;
		Boolean foundStopPt = false;

		while (!foundStartPt) {
			if (scanner.hasNext()) {
				String fileString = scanner.next();
				if (fileString.equals(startPt)) {
					foundStartPt = true;
				}
			}
		}
		while (!foundStopPt && foundStartPt) {
			if (scanner.hasNext()) {
				String fileString = scanner.next();
				if (fileString.equals(stopPt)) {
					foundStopPt = true;
					break;
				}
				if (fileString.startsWith("a") || fileString.startsWith("c") || fileString.startsWith("g")
						|| fileString.startsWith("t") || fileString.startsWith("n")) {
					dataString += fileString;
				}
			}
		}
		// break data into moving window groups of sequenceLength size
		// for(int i=0; i<dataString.length()-sequenceLength; i++){ // for full list of
		// data
		for (int i = 0; i < 50; i++) { // for testing purposes
			if (validSequence(dataString, i, sequenceLength)) {
				System.out.println(objectString(dataString, i, sequenceLength)); // for testing purposes
				// Build Data Object for Tree and insert into tree
			}
		}
		// System.out.println(dataString);
		// System.out.println(dataString.length());
		scanner.close();

		if (args.length == 4) {
			if (args[0].equals("0")) {
				// no cache
			} else {
				System.out.println("Cache option selected, but no cache Size was not provided.");
				useage();
			}
		} else if (args.length == 5) {
			if (args[0].equals("0")) {
				try {
					bTree.setDebugLevel(Integer.parseInt(args[4]));
				} catch (Exception e) {
					System.out.println(args[4] + " is not an integer.");
					useage();
				}
			} else if (args[0].equals("1")) {
				try {
					bTree.setCacheSize(Integer.parseInt(args[4]));
				} catch (Exception e) {
					System.out.println(args[4] + " is not an integer.");
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
					System.out.println(args[4] + " is not an integer.");
					useage();
				}
				try {
					bTree.setDebugLevel(Integer.parseInt(args[5]));
				} catch (Exception e) {
					System.out.println(args[5] + " is not an integer.");
					useage();
				}
			} else {
				System.out.println("Cache option not selected " + args[0] + ", but cache size provided " + args[4] + ".");
				useage();
			}
		} else {
			useage();
		}
	}
}