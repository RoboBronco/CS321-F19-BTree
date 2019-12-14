import java.io.IOException;
import java.io.RandomAccessFile;

public class BTreeNode {

	private Boolean leafNode; // leaf nose status
	private int metaDataSize; // size in bytes of MetaData
	private int nodeSize; // overall size in bytes of the node
	private int numObjects; // number of objects in the node currently
	private int locInFile; // address/byte-offset in file
	private int parentNode; // address of parent node in file
	private int nodeDegree; // degree value for node
	public int[] children; // each is address in file of child nodes
	public TreeObject[] objects; // keys/objects stored in each node

	public BTreeNode(int address, int degree) { // Make a new node
		leafNode = true;
		metaDataSize = 1 + 4 + 4 + 4 + 4 + 4 + 4;
		nodeSize = metaDataSize + ((2 * degree) * 4) + 4 + ((2 * degree - 1) * (8 + 4 + 4));
		numObjects = 0;
		locInFile = address;
		parentNode = 0;
		nodeDegree = degree;
		children = new int[degree * 2];
		objects = new TreeObject[(degree * 2) - 1];
	}

	public BTreeNode(int address, int degree, RandomAccessFile file) { // Load a node using RandomAccessFile
		try {
			RandomAccessFile raf = file;
			raf.seek(address);

			leafNode = raf.readBoolean();
			metaDataSize = raf.readInt();
			nodeSize = raf.readInt();
			numObjects = raf.readInt();
			locInFile = raf.readInt();
			parentNode = raf.readInt();
			nodeDegree = raf.readInt();
			children = new int[degree * 2];
			objects = new TreeObject[degree * 2 - 1];

			for (int i = 0; i < children.length; i++) {
				children[i] = raf.readInt();
			}

			raf.seek(locInFile + metaDataSize + ((2 * nodeDegree) * 4) + 4);
			for (int j = 0; j < numObjects; j++) {
				Long reloadData = raf.readLong();
				int reloadFrequency = raf.readInt();
				int reloadSedLen = raf.readInt();
				TreeObject reloadObj = new TreeObject(reloadData, reloadSedLen);
				reloadObj.setFrequency(reloadFrequency);
				objects[j] = reloadObj;
			}
		} catch (IOException e) {
			System.out.println("Error reading node from RandomAccessFile. " + e);
		}
	}

	public void insertObject(TreeObject object, int index) { // Insert an object into a node
		if (objects[index] == null) {
			objects[index] = object;
		} else if (objects[index].equals(object)) {
			objects[index].incrementFrequency(object.getFrequency());
		} else {
			objects[index] = object;
		}
	}

	public TreeObject relocateObject(int index) { // Remove a TreeObject from a node (used for splitting nodes)
		TreeObject relocateObj = objects[index];
		objects[index] = null;
		return relocateObj;
	}

	public int relocateChild(int index) { // Remove child pointers/addresses (used for splitting)
		int relocateChild = children[index];
		children[index] = -1;
		return relocateChild;
	}

	public Boolean isLeaf() { // Boolean value to check leaf status
		return leafNode;
		// if (children[0] <= 0) {
		// return true;
		// } else {
		// return false;
		// }
	}

	public void setLeaf(boolean leafStatus) { // Used to set leaf status
		leafNode = leafStatus;
	}

	public void setParent(int parentLocation) { // Set parent address of current node
		parentNode = parentLocation;
	}

	public int getParent() { // Returns the address of the parent node to the current node
		return parentNode;
	}

	public void incrementNumObjects() { // Increase the number-of-objects counter in a node
		numObjects++;
	}

	public void decrementNumObjects() { // Decrease the number-of-objects counter in a node
		numObjects--;
	}

	public void setNumObjects(int newValue) { // Set the number of objects in a node (used while splitting nodes)
		numObjects = newValue;
	}

	public int numObjects() { // Returns the number of objects in a node
		return numObjects;
	}

	public int nodeAddress() { // Returns the address/byte-offset in the file
		return locInFile;
	}

	public int nodeSize() { // Returns the node size in bytes
		return nodeSize;
	}

	public void writeToFile(RandomAccessFile file) { // Writes the node data to the file with RandomAccessFile
		try {
			RandomAccessFile raf = file;
			raf.seek(locInFile);

			raf.writeBoolean(leafNode);
			raf.writeInt(metaDataSize);
			raf.writeInt(nodeSize);
			raf.writeInt(numObjects);
			raf.writeInt(locInFile);
			raf.writeInt(parentNode);
			raf.writeInt(nodeDegree);

			for (int i = 0; i < children.length; i++) {
				raf.writeInt(children[i]);
			}

			raf.seek(locInFile + metaDataSize + ((2 * nodeDegree) * 4) + 4);
			for (int j = 0; j < numObjects; j++) {
				raf.writeLong(objects[j].getData());
				raf.writeInt(objects[j].getFrequency());
				raf.writeInt(objects[j].getSequenceLength());
			}

		} catch (IOException e) {
			System.out.println("Error writing node to RandomAccessFile. " + e);
		}
	}

	public Boolean containsObject(TreeObject searchObject) { // returns true if searchObject is found in the node
		if (numObjects == 0) {
			return false;
		}
		for (int i = 0; i < numObjects; i++) {
			if (objects[i].equals(searchObject)) {
				return true;
			}
		}
		return false;
	}

	public int containsObjectAtIndex(TreeObject searchObject) { // returns true if searchObject is found in the node
		int searchObjectIndex = -1;
		if (numObjects == 0) {
			return searchObjectIndex;
		}
		for (int i = 0; i < numObjects; i++) {
			if (objects[i].equals(searchObject)) {
				return i;
			}
		}
		return searchObjectIndex;
	}

	public String printNode() {
		String returnString = "";
		for (int i = 0; i < numObjects; i++) {
			returnString += objects[i].getData();
			returnString += " : ";
		}
		return returnString;
	}
}