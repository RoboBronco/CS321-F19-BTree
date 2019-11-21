public class TreeObject{

    private Long dataString;
    private int frequency;

    public TreeObject(Long dataS){
        dataString = dataS;
        frequency = 1;
    }

    public void incrementFrequency(){
        frequency ++;
    }

    public int getFrequency(){
        return frequency;
    }

    public Long getData(){
        return dataString;
    }

    public boolean equals(TreeObject object2){
        if(dataString.equals(object2.getData())){
            return true;
        } else {
            return false;
        }
    }
}