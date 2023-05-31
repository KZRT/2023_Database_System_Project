package database.internal;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class Buffer {
    private final ArrayList<Long> blocks;
    private int index;
    private File file;
    private String fileName;
    private BufferType type;
    private final static String fileLocation = "/buffer";
    public final static int BLOCK_SIZE = 4;

    public Buffer(int index){
        this.blocks = new ArrayList<>(BLOCK_SIZE);
        this.index = index;
        this.type = BufferType.FREE;
        this.file = null;
    }

    public Buffer(int index, BufferType type){
        this.blocks = new ArrayList<>(BLOCK_SIZE);
        this.index = index;
        this.type = type;
        this.file = null;
    }

    public Buffer(int index, BufferType type, DataType dataType){
        this.blocks = new ArrayList<>(BLOCK_SIZE);
        this.index = index;
        this.fileName = fileLocation + dataType.toString();
        this.file = new File(fileName + index);
        this.type = type;
    }

    public Buffer(int index, BufferType type, String fileName){
        this.blocks = new ArrayList<>(BLOCK_SIZE);
        this.index = index;
        this.fileName = fileName;
        this.file = new File(this.fileName + index);
        this.type = type;
    }

    public ArrayList<Long> getBlocks() {
        return blocks;
    }

    public long getBlock(int index){
        return blocks.get(index);
    }

    public int getIndex() {
        return index;
    }

    public BufferType getType() {
        return type;
    }

    public void setBlock(int index, long block){
        blocks.set(index, block);
    }

    public void setType(BufferType type){
        this.type = type;
    }

    public void setFile(String fileName){
        this.fileName = fileName;
        this.file = new File(fileLocation + fileName + index);
    }

    public boolean isFull(){
        return switch (type) {
            case FREE -> false;
            case WRITE, PINNED -> true;
        };
    }

    public boolean writeFullBuffer() throws IOException{
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
        for(long block : blocks){
            dataOutputStream.writeLong(block);
        }
        dataOutputStream.close();
        clearBuffer();
        return true;
    }

    public boolean addBlock(long block){
        blocks.add(block);
        return true;
    }
    
    protected void clearBuffer(){
        blocks.clear();
        this.type = BufferType.FREE;
    }

    public void readBuffer() throws IOException{
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
        for(long block : blocks){
            dataOutputStream.writeLong(block);
        }
        dataOutputStream.close();
        blocks.clear();
    }

    public void readBuffer(BufferType type) throws IOException{
        readBuffer();
        this.type = type;
    }

    public void readBuffer(int index) throws IOException{
        this.index = index;
        this.file = new File(fileName + index);
        readBuffer();
    }

    public void readBuffer(int index, BufferType type) throws IOException{
        readBuffer(index);
        this.type = type;
    }
}