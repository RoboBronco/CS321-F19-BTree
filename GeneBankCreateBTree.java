import java.util.Scanner;
import java.io.*;

public class GeneBankCreateBTree {

	public int degree, sequenceLength;
	public int cacheSize;
	public File fileName;
	public String fileString;
	public BTree workingBTree;
	public boolean cache = false;
	public boolean debug = false;

	public int getDegree() {
		return this.degree;
	}

	public int getSequenceLength() {
		return this.sequenceLength;
	}

	public int getCacheSize() {
		return this.cacheSize;
	}

	public File getFile() {
		return this.fileName;
	}

	public String getFileString() {
		return this.fileString;
	}

	public BTree getBTree() {
		return this.workingBTree;
	}

	public boolean isCache() {
		return this.cache;
	}

	public boolean isDebug() {
		return this.debug;
	}

	public int optimalDegree(int degree) {
		return 127;
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
			this.degree = optimalDegree(this.degree);
		if (this.sequenceLength < 1 || this.sequenceLength > 31)
			useage();
		this.fileString = fileName;
		this.fileName = new File(fileName);
		if (!this.fileName.exists() || this.fileName.isDirectory())
			useage();
	}

	// constructor with cache and debug
	public GeneBankCreateBTree(String cache, String degree, String fileName, String sequenceLength, String cacheSize, String debugLevel) throws FileNotFoundException {
		check(degree, fileName, sequenceLength);
		if (!cache.equals("1")) {
			useage();
		} else {
			this.cache = true;
		}
		try {
			this.cacheSize = Integer.parseInt(cacheSize);
			if (this.cacheSize < 1)
				useage();
		} catch (Exception e) {
			useage();
		}
		if (debugLevel.equals("0")) {
			// debug level 0 standard useage messages
		} else if (debugLevel.equals("1")) {
			this.debug = true;
			// debug level 1, dump file
		} else {
			useage();
		}
		this.workingBTree = new BTree(getFileString(), getSequenceLength(), getDegree(), isCahce());
	}

	// constructor with cache or debug
	public GeneBankCreateBTree(String cache, String degree, String fileName, String sequenceLength, String cacheSizeOrDebug) throws FileNotFoundException {
		check(degree, fileName, sequenceLength);
		if (cache.equals("0")) {
			if (cacheSizeOrDebug.equals("0")) {
				// debug level 0 standard useage messages
			} else if (cacheSizeOrDebug.equals("1")) {
				this.debug = true;
				// debug level 1, dump file
			} else {
				useage();
			}
		} else if (cache.equals("1")) {
			this.cache = true;
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
		this.workingBTree = new BTree(getFileString(), getSequenceLength(), getDegree(), isCache());
	}

	// constructor without cache or debug
	public GeneBankCreateBTree(String cache, String degree, String fileName, String sequenceLength)
			throws FileNotFoundException {
		check(degree, fileName, sequenceLength);
		if (!cache.equals("0")) {
			useage();
		}
		this.workingBTree = new BTree(getFileString(), getSequenceLength(), getDegree(), isCache());
	}

	public static void useage() {
		System.out.println(
				"Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
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
			if (data.charAt(i) == 'n' || data.charAt(i) == 'N') {
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
		// System.out.println(stringToLong(objString)); // for testing purposes
		return objString;
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
		GeneBankCreateBTree geneBank = null;
		if (args.length == 4) {
			geneBank = new GeneBankCreateBTree(args[0], args[1], args[2], args[3]);
		} else if (args.length == 5) {
			geneBank = new GeneBankCreateBTree(args[0], args[1], args[2], args[3], args[4]);
		} else if (args.length == 6) {
			geneBank = new GeneBankCreateBTree(args[0], args[1], args[2], args[3], args[4], args[5]);
		} else {
			useage();
		}

		// parse through the gbk file
		BTree workingBTree = geneBank.getBTree();
		int seqLength = geneBank.getSequenceLength();
		File file = geneBank.getFile();
		Scanner scanner = new Scanner(file);
		String startPt = "ORIGIN";
		String stopPt = "//";
		String dataString = "";
		Boolean foundStartPt = false;
		Boolean foundStopPt = false;

		while (scanner.hasNext()) {
			String fileString = scanner.next();
			if (fileString.equals(startPt))
				foundStartPt = true;
			while (foundStartPt && !foundStopPt) {
				if (fileString.equals(stopPt)) {
					foundStopPt = true;
					dataString += "n";
				} else {
					fileString = scanner.next();
					if (fileString.startsWith("a") || fileString.startsWith("c") || fileString.startsWith("g")
							|| fileString.startsWith("t") || fileString.startsWith("n")) {
						dataString += fileString;
					}
				}
			}
			foundStartPt = false;
			foundStopPt = false;
		}

		// Check for cache boolean then build cache if necessary
		if (geneBank.isCache()){
			BTreeCache treeCache = new BTreeCache(geneBank.cacheSize()); // How do I pass in the cache size here?
		}

		// break data into moving window groups of sequenceLength size
		for (int i = 0; i < dataString.length() - seqLength; i++) {
			if (validSequence(dataString, i, seqLength)) {
				Long newData = stringToLong(objectString(dataString, i, seqLength));
				TreeObject newObject = new TreeObject(newData, seqLength);
				// Need to incorporate cache in this area...
				if(geneBank.isCache()) {
					// I'm not sure what is going on here... -Cody
					// if(geneBank.getCacheSize().check(newObject)) {
					// 	newObject.incrementFrequency(newObject.getFrequency());
					// }else{
					// 	//newCache.add();
					// }
					if(treeCache.searchItem(newObject)){
						int address = treeCache.removeFirstNode().nodeAddress();
						BTreeNode updateNode = workingBTree.loadNode(address);
                        for( int j=0; j<updateNode.numObjects(); j++){
                            if (newObject.equals(updateNode.objects[j])){
                                updateNode.insertObject(newObject,j);
                                treeCache.add(updateNode);
                                workingBTree.DiskWrite(updateNode);
                            }
                        }
					} else {
						workingBTree.insert(newObject);
					}
				} else {
					workingBTree.insert(newObject);
				}
			}
		}

		if (geneBank.isDebug()) {
			workingBTree.debug();
		}

		scanner.close();
		workingBTree.closeDownBTree();
	}
}
