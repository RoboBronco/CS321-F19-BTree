public class TreeObject{

    private Long dataString;
    private int frequency;
    private int sequenceLength;

    public TreeObject(Long dataS, int seqLength){
        dataString = dataS;
        sequenceLength = seqLength;
        frequency = 1;
    }

    public void incrementFrequency(){
        frequency ++;
    }

    public int getFrequency(){
        return frequency;
    }

    public void setFrequency(int freq){
        frequency = freq;
    }

    public int getSequenceLength(){
        return sequenceLength;
    }

    public void setSequenceLength(int seqLen){
        sequenceLength = seqLen;
    }

    public Long getData(){
        return dataString;
    }

    public void setData(Long dataLong){
        dataString = dataLong;
    }

    public boolean equals(TreeObject object2){
        if(dataString.equals(object2.getData())){
            return true;
        } else {
            return false;
        }
    }

    public String toStringACGT(){
        String binaryString = Long.toBinaryString(dataString);
        int totalLength = sequenceLength * 2;
        if (binaryString.length() != totalLength){
            int diffLength = totalLength - binaryString.length();
            String binaryZeros = "";
            for (int i=0; i<diffLength; i++){
                binaryZeros += "0";
            }
            binaryString = binaryZeros + binaryString;
        }
        String stringACGT = "";
        for (int j=0; j<binaryString.length()-1; j+=2){
            if (binaryString.subSequence(j,j+1) == "00"){
                stringACGT += "a";
            } else if (binaryString.subSequence(j,j+1) == "01"){
                stringACGT += "c";
            } else if (binaryString.subSequence(j,j+1) == "10"){
                stringACGT += "g";
            } else if (binaryString.subSequence(j,j+1) == "11"){
                stringACGT += "t";
            }
        }
        String outputString = stringACGT + ": " + frequency;
        return outputString;
    }
}