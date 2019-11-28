import java.util.Scanner;
import java.io.*;

public class GeneBankCreateBTree {

	public int degree, sequenceLength, cacheSize;
	public File fileName;

	public int getDegree() {
		return this.degree;
	}

	public File getFile() {
		return this.fileName;
	}

	public int getSequenceLength() {
		return this.sequenceLength;
	}

	public int getCacheSize() {
		return this.cacheSize;
	}

	public void check(String degree, String fileName, String sequenceLength) {
		try {
			this.degree = Integer.parseInt(degree);
			this.sequenceLength = Integer.parseInt(sequenceLength);
		} catch (Exception e) {
			useage();
		}
		if (this.degree < 0)
			useage();
		else if (this.degree == 0)
			this.degree = 127;
		if (this.sequenceLength < 1 || this.sequenceLength > 31)
			useage();
		this.fileName = new File(fileName);
		if(!this.fileName.exists() || this.fileName.isDirectory()) 
			useage();
	}

	//constructor with cache and debug
	public GeneBankCreateBTree(String cache, String degree, String fileName, String sequenceLength, String cacheSize, String debugLevel) {
		check(degree, fileName, sequenceLength);
		if (!cache.equals("1")) {
			useage();
		}
		try {
			this.cacheSize = Integer.parseInt(cacheSize);
			if (this.cacheSize < 1)
			useage();
		} catch (Exception e) {
			useage();
		}
		if (debugLevel.equals("0")) {
			//debug level 0
		} else if (debugLevel.equals("1")) {
			//debug level 1, dump file
		} else {
			useage();
		}
	}
	//constructor with cache or debug
	public GeneBankCreateBTree(String cache, String degree, String fileName, String sequenceLength, String cacheSizeOrDebug) {
		check(degree, fileName, sequenceLength);
		if (cache.equals("0")) {	
			if (cacheSizeOrDebug.equals("0")) {
				//debug level 0
			} else if (cacheSizeOrDebug.equals("1")) {
				//debug level 1, dump file
			} else {
				useage();
			}
		} else if (cache.equals("1")) {
			try {
				this.cacheSize = Integer.parseInt(cacheSizeOrDebug);
				if (this.cacheSize < 1)
					useage();
			} catch (Exception e) {
				useage();
			}
		} else {
			useage();
		}
	}
	//constructor without cache or debug
	public GeneBankCreateBTree(String cache, String degree, String fileName, String sequenceLength) {
		if (!cache.equals("0")) {
			useage();
		}
		check(degree, fileName, sequenceLength);
	}

	public static void useage() {
		System.out.println("Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
		System.exit(-1);
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
		GeneBankCreateBTree bTree = null;
		if (args.length == 4) {
			bTree = new GeneBankCreateBTree(args[0], args[1], args[2], args[3]);
		} else if (args.length == 5) {
			bTree = new GeneBankCreateBTree(args[0], args[1], args[2], args[3], args[4]);
		} else if (args.length == 6) {
			bTree = new GeneBankCreateBTree(args[0], args[1], args[2], args[3], args[4], args[5]);
		} else {
			useage();
		}
		// Where is the actual BTree started??
		// BTree actualBTree = new BTree(String fileName, int sequenceLength, int degreeT)

		// parse through the gbk file
		File file = bTree.getFile();
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
		// for(int i=0; i<dataString.length()-sequenceLength; i++){ // for full list of data
		for (int i = 0; i < 50; i++) { // for testing purposes
			if (validSequence(dataString, i, bTree.getSequenceLength())) {
				System.out.println(objectString(dataString, i, bTree.getSequenceLength())); // for testing purposes
				// Build Data Object for Tree and insert into tree
			}
		}
		// System.out.println(dataString);
		// System.out.println(dataString.length());
		scanner.close();
	}
}
