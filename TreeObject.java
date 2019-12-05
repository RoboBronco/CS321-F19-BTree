public class TreeObject {

	private Long dataString;	// Main data object
	private int frequency;	// Counter for number of occurances
	private int sequenceLength;	// Length of the data for converting back to ACGT values

	public TreeObject(Long dataS, int seqLength) { // Makes a new TreeObject
		dataString = dataS;
		sequenceLength = seqLength;
		frequency = 1;
	}

	public void incrementFrequency(int objFreq) {	// Increments frequency by the input value
		frequency += objFreq;
	}

	public int getFrequency() {	// Returns the frequency
		return frequency;
	}

	public void setFrequency(int freq){	// Used to set frequency (may not be used in final product)
		frequency = freq;
	}

	public int getSequenceLength() {	// Returns the sequence length for converting back to ACGT
		return sequenceLength;
	}

	public void setSequenceLength(int seqLen) {	// Used to set sequence length (may not be used in final product)
		sequenceLength = seqLen;
	}

	public Long getData() {	// Returns the main object data - the Long value
		return dataString;
	}

	public void setData(Long dataLong) {	// Sets the Long data value
		dataString = dataLong;
	}

	public boolean equals(TreeObject object2) {	// Equals comparitor -> returns true if two Long values are the same
		if (dataString.equals(object2.getData())) {
			return true;
		} else {
			return false;
		}
	}

	public String toStringACGT() {	// Builds and returns a string of ACGT values from the Long data
		String binaryString = Long.toBinaryString(dataString);
		int totalLength = sequenceLength * 2;
		if (binaryString.length() != totalLength) {
			int diffLength = totalLength - binaryString.length();
			String binaryZeros = "";
			for (int i = 0; i < diffLength; i++) {
				binaryZeros += "0";
			}
			binaryString = binaryZeros + binaryString;
		}
		String stringACGT = "";
		for (int j = 0; j < binaryString.length() - 1; j += 2) {
			if (binaryString.subSequence(j, j + 2).equals("00")) {
				stringACGT += "a";
			} else if (binaryString.subSequence(j, j + 2).equals("01")) {
				stringACGT += "c";
			} else if (binaryString.subSequence(j, j + 2).equals("10")) {
				stringACGT += "g";
			} else if (binaryString.subSequence(j, j + 2).equals("11")) {
				stringACGT += "t";
			}
		}
		String outputString = stringACGT + ": " + frequency;
		return outputString;
	}
}