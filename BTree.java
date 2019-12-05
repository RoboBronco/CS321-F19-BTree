import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class BTree {
	private int degree;
	private int seqLength;
	private Boolean useCache;
	private int nextNodeAddress;
	private BTreeNode root;
	private int rootAddress;
	private int nodeSize;
	private RandomAccessFile raf;
	private PrintWriter printer1;

	public BTree(String fileName, int sequenceLength, int degreeT, Boolean cacheBool) throws FileNotFoundException {
		// if (fileName.endsWith(".gbk")) {
			degree = degreeT;
			seqLength = sequenceLength;
			useCache = cacheBool;
			// nextNodeAddress = 4 + 4 + 4 + 4 + 4;
			nextNodeAddress = 112; // Temporary value that is intended to be the MetaData size of the BTree
			String filePath = fileName + ".btree.data." + seqLength + "." + degree;
			raf = new RandomAccessFile(filePath, "rw");
			root = new BTreeNode(nextNodeAddress, degree);
			rootAddress = root.nodeAddress();
			nodeSize = root.nodeSize();
			nextNodeAddress += nodeSize;
		// } else {
		// 	try {
		// 		degree = degreeT;
		// 		seqLength = sequenceLength;
		// 		raf = new RandomAccessFile(fileName, "rw");
		// 		int rafStartIndex = 4 + 4;
		// 		raf.seek(rafStartIndex);
		// 		nextNodeAddress = raf.readInt();
		// 		rootAddress = raf.readInt();
		// 		root = new BTreeNode(rootAddress, degree, raf);
		// 		nodeSize = raf.readInt();
		// 	} catch (IOException e) {
		// 		System.out.println("Error reading Metadata for BTree from RandomAccessFile. " + e);
		// 	}
		//  }
	}

	public BTree(String fileName) throws FileNotFoundException {
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

	public void insert(TreeObject k) {
		BTreeNode r = root;
		for (int i = 0; i < r.numObjects(); i++) {
			if (k.equals(r.objects[i])) {
				r.objects[i].incrementFrequency(k.getFrequency());
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

	public void splitChild(BTreeNode x, int i, BTreeNode y) {
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
		DiskWrite(y);
		DiskWrite(z);
		DiskWrite(x);
	}

	public void insertNonFull(BTreeNode x, TreeObject k) {
		for (int m = 0; m < x.numObjects(); m++) {
			if (k.equals(x.objects[m])) {
				x.objects[m].incrementFrequency(k.getFrequency());
				// x.insertObject(k,m);
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

	public Boolean search(BTreeNode x, TreeObject k) {
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

	public void DiskWrite(BTreeNode writeNode) {
		writeNode.writeToFile(raf);
	}

	public void closeRandomAccessFile() {
		try {
			raf.close();
		} catch (IOException e) {
			System.out.println("Issue closing RandomAccessFile. " + e);
		}
	}

	public BTreeNode root() {
		return root;
	}

	public void printTree(BTreeNode printNode) {
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
    
    public void setDumpWriter() throws FileNotFoundException {
        printer1 = new PrintWriter("dump");
    }

	public void printTreeToFile(BTreeNode printNode) {
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

	public void closePrinter() {
		printer1.close();
	}

	public void writeMetaData() {
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
}