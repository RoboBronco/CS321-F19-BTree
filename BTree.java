import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class BTree {
	private int degree;
	private int seqLength;
	private Boolean useCache;
	private int nextNodeAddress;
	private BTreeNode root, parent, leftChild, rightChild, currentNode;
	private int rootAddress;
	private int nodeSize;
	private RandomAccessFile raf;
	private PrintWriter printer1;
	private BTreeCache bTreeCache;

	public BTree(String fileName, int sequenceLength, int degreeT, Boolean cacheBool) throws FileNotFoundException {	// Builds a new BTree
			degree = degreeT;
			seqLength = sequenceLength;
			useCache = cacheBool;
			// nextNodeAddress = 4 + 4 + 1 + 4 + 4 + 4;
			nextNodeAddress = 30; // Temporary value that is intended to be the MetaData size of the BTree
			String filePath = fileName + ".btree.data." + seqLength + "." + degree;
			raf = new RandomAccessFile(filePath, "rw");
			root = new BTreeNode(nextNodeAddress, degree);
			rootAddress = root.nodeAddress();
			nodeSize = root.nodeSize();
			nextNodeAddress += nodeSize;
	}

	public BTree(String fileName) throws FileNotFoundException {	// Reloads a BTree that has been writtent to file with RandomAccessFile
		try {
			raf = new RandomAccessFile(fileName, "rw");
			int rafStartIndex = 0;
			raf.seek(rafStartIndex);

			degree = raf.readInt();
			seqLength = raf.readInt();
			useCache = raf.readBoolean();
			nextNodeAddress = raf.readInt();
			rootAddress = raf.readInt();
			root = new BTreeNode(rootAddress, degree, raf);
			nodeSize = raf.readInt();
		} catch (IOException e) {
			System.out.println("Error reading Metadata for BTree from RandomAccessFile. " + e);
		}
	}

	public void insert(TreeObject k) {	// Used to insert a new object into the BTree
		BTreeNode r = root;
		for (int i = 0; i < r.numObjects(); i++) {
			if (k.equals(r.objects[i])) {
				r.objects[i].incrementFrequency(k.getFrequency());
				if (useCache){
					//currentNode = r;
					//compareAndAddToCache();
					bTreeCache.add(r);
				}
				return;
			}
		}
		if (r.numObjects() == (2 * degree) - 1) {
			BTreeNode s = new BTreeNode(nextNodeAddress, degree);
			nextNodeAddress += nodeSize;
			root = s;
			rootAddress = root.nodeAddress();
			s.setLeaf(false);
			s.children[0] = r.nodeAddress();
			r.setParent(s.nodeAddress());
			splitChild(s, 0, r);
			insertNonFull(s, k);
		} else {
			insertNonFull(r, k);
		}
	}

	public void splitChild(BTreeNode x, int i, BTreeNode y) {	// Splits a node that is at maximum object capacity
		BTreeNode z = new BTreeNode(nextNodeAddress, degree);
		nextNodeAddress += nodeSize;
		z.setLeaf(y.isLeaf());
		z.setParent(y.getParent());
		z.setNumObjects(degree - 1);
		for (int h = 0; h < degree - 1; h++) {
			z.insertObject(y.relocateObject(h + degree), h);
		}
		if (!y.isLeaf()) {
			for (int j = 0; j < degree; j++) {
				z.children[j] = y.relocateChild(j + degree);
			}
		}
		y.setNumObjects(degree - 1);
		for (int k = x.numObjects(); k > i; k--) {
			x.children[k + 1] = x.children[k];
		}
		x.setLeaf(false);
		x.children[i + 1] = z.nodeAddress();
		for (int m = x.numObjects() - 1; m >= i; m--) {
			x.objects[m + 1] = x.objects[m];
		}
		x.objects[i] = y.relocateObject(degree - 1);
		x.incrementNumObjects();
		if (useCache){
			// parent = x;
			// leftChild = y;
			// rightChild = z;
			bTreeCache.add(x);
			bTreeCache.add(y);
			bTreeCache.add(z);
		}
		DiskWrite(y);
		DiskWrite(z);
		DiskWrite(x);		
	}

	public void insertNonFull(BTreeNode x, TreeObject k) {	// Inserts an object into a node that has space available
		for (int m = 0; m < x.numObjects(); m++) {
			if (k.equals(x.objects[m])) {
				x.objects[m].incrementFrequency(k.getFrequency());
				// x.insertObject(k,m);
				if (useCache){
					//currentNode = x;
					//DiskWrite(x);
					//compareAndAddToCache();
					bTreeCache.add(x);
				}
				DiskWrite(x);
				return;
			}
		}
		int i = x.numObjects() - 1;
		if (x.isLeaf()) {
			while (i >= 0 && k.getData() < x.objects[i].getData()) {
				x.objects[i + 1] = x.objects[i];
				i--;
			}
			x.insertObject(k, (i + 1));
			x.incrementNumObjects();
			if (useCache){
				//currentNode = x;
				//DiskWrite(x);
				//compareAndAddToCache();
				bTreeCache.add(x);
			}
			DiskWrite(x);
		} else {
			while (i >= 0 && k.getData() < x.objects[i].getData()) {
				i--;
			}
			i++;
			if (x.children[0] == 0) {
				System.out.println("Error with node leaf status, no children for a non-leaf node at file address: "
						+ x.nodeAddress());
				return;
			}
			BTreeNode childNode = new BTreeNode(x.children[i], degree, raf); // reads node - a child node of x
			for (int n = 0; n < childNode.numObjects(); n++) {
				if (k.equals(childNode.objects[n])) {
					childNode.objects[n].incrementFrequency(k.getFrequency());
					if (useCache){
						// currentNode = childNode;
						// DiskWrite(childNode);
						// compareAndAddToCache();
						bTreeCache.add(childNode);
					}
					DiskWrite(childNode);
					return;
				}
			}
			if (childNode.numObjects() == (2 * degree) - 1) {
				splitChild(x, i, childNode);
				if (k.getData() > x.objects[i].getData()) {
					i++;
					childNode = new BTreeNode(x.children[i], degree, raf);
				}
			}
			insertNonFull(childNode, k);
		}
	}

	public Boolean search(BTreeNode x, TreeObject k) { // NEEDS TO BE FINALIZED!!!
		// need to include cache useage and figure out expected output
		int i = 0;
		while (i < x.numObjects() && k.getData() > x.objects[i].getData()) {
			i++;
		}
		if (i < x.numObjects() && k.equals(x.objects[i])) {
			return true;
		}
		if (x.isLeaf()) {
			System.out.println("Data not found");
			return false;
		} else {
			BTreeNode childNode = new BTreeNode(x.children[i], degree, raf);
			return search(childNode, k);
		}
	}

	public BTreeNode root() {	// Returns the root node of the BTree
		return root;
	}

	public BTreeNode loadNode(int addressInFile){	// Loads a node from the RandomAccessFile -> used when using a cache
		BTreeNode loadedNode = new BTreeNode(addressInFile, degree);
		return loadedNode;
	}

	public void DiskWrite(BTreeNode writeNode) {	// Writes the BTreeNode to file with RandomAccessFile
		writeNode.writeToFile(raf);
	}

	private void writeMetaData() {	// Writes BTree MetaData to file with RandomAccessFile
		try {
			raf.writeInt(degree);
			raf.writeInt(seqLength);
			raf.writeBoolean(useCache);
			raf.writeInt(nextNodeAddress);
			raf.writeInt(rootAddress);
			raf.writeInt(nodeSize);
		} catch (IOException e) {
			System.out.println("Error writing BTree Metadata to RandomAccessFile. " + e);
		}
	}

	private void closeRandomAccessFile() {	// Closes the RandomAccessFile
		try {
			raf.close();
		} catch (IOException e) {
			System.out.println("Issue closing RandomAccessFile. " + e);
		}
	}

	public void closeDownBTree(){	// Final items that are done when finished with a BTree
		DiskWrite(root);
		writeMetaData();
		closeRandomAccessFile();
	}

	public void printTree(BTreeNode printNode) {	// Prints out the BTree in order (expects input of root node to print entire BTree)
		int i = 0;
		while (i < printNode.numObjects()) {
			if (printNode.children[i] > 0) {
				BTreeNode child1 = new BTreeNode(printNode.children[i], degree, raf);
				printTree(child1);
			}
			System.out.println(printNode.objects[i].getData() + " -- " + printNode.objects[i].toStringACGT());
			i++;
		}
		if (printNode.children[i] > 0) {
			BTreeNode child2 = new BTreeNode(printNode.children[printNode.numObjects()], degree, raf);
			printTree(child2);
		}
    }
    
    private void setDumpWriter() throws FileNotFoundException {	// Used to setup PrintWriter for dump file -> used for Debug
        printer1 = new PrintWriter("dump");
    }

	private void printTreeToFile(BTreeNode printNode) {	// Prints BTree to dump file -> used for Debug
		int i = 0;
		while (i < printNode.numObjects()) {
			if (printNode.children[i] > 0) {
				BTreeNode child1 = new BTreeNode(printNode.children[i], degree, raf);
				printTreeToFile(child1);
			}
			printer1.println(printNode.objects[i].toStringACGT());
			i++;
		}
		if (printNode.children[i] > 0) {
			BTreeNode child2 = new BTreeNode(printNode.children[printNode.numObjects()], degree, raf);
			printTreeToFile(child2);
		}
	}

	private void closePrinter() {	// Closes the PrintWriter
		printer1.close();
	}

	public void debug() throws FileNotFoundException {	// Combines all methods for Debug into one
		setDumpWriter();
		printTreeToFile(root);
		closePrinter();
	}

	public void setCache(BTreeCache cache){	// Sets the cache for cache usage option
		bTreeCache = cache;
		useCache = true;
	}

	// private void compareAndAddToCache(){	// Compares recently changed nodes and adds them accordingly to cache
	// 	if (parent == null){
	// 		bTreeCache.add(currentNode);
	// 		DiskWrite(currentNode);
	// 		currentNode = null;
	// 		//return;
	// 	} else {
	// 		if (currentNode.nodeAddress() == parent.nodeAddress()){
	// 			bTreeCache.add(currentNode);
	// 			parent = null;
	// 			currentNode = null;
	// 			bTreeCache.add(leftChild);
	// 			leftChild = null;
	// 			bTreeCache.add(rightChild);
	// 			rightChild = null;
	// 			//return;
	// 		} else if (currentNode.nodeAddress() == leftChild.nodeAddress()){
	// 			bTreeCache.add(parent);
	// 			parent = null;
	// 			bTreeCache.add(currentNode);
	// 			leftChild = null;
	// 			currentNode = null;
	// 			bTreeCache.add(rightChild);
	// 			rightChild = null;
	// 			//return;
	// 		} else if (currentNode.nodeAddress() == rightChild.nodeAddress()){
	// 			bTreeCache.add(parent);
	// 			parent = null;
	// 			bTreeCache.add(leftChild);
	// 			leftChild = null;
	// 			bTreeCache.add(currentNode);
	// 			rightChild = null;
	// 			currentNode = null;
	// 			//return;
	// 		} else {
	// 			bTreeCache.add(parent);
	// 			parent = null;
	// 			bTreeCache.add(leftChild);
	// 			leftChild = null;
	// 			bTreeCache.add(rightChild);
	// 			rightChild = null;
	// 			bTreeCache.add(currentNode);
	// 			currentNode = null;
	// 			//return;
	// 		}
	// 	}
	// }

	// public void updateNode(BTreeNode nodeToUpdate){	// Updates node data -> used when node is returned/removed from cache
	// 	nodeToUpdate.writeToFile(raf);
	// }

	public int getSequenceLength(){
		return seqLength;
	}
}