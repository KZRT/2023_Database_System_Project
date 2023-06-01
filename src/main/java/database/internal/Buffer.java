package database.internal;
import java.io.*;
import java.util.ArrayList;

class Buffer {
    private final ArrayList<Long> blocks;
    private int blockIndex;
    private File file;
    private BufferType type;
    private final static String fileLocation = "/buffer";
    public final static int BLOCK_SIZE = 4;

    protected Buffer(BufferType type, String fileName){
        this.blocks = new ArrayList<>(BLOCK_SIZE);
        this.blockIndex = 0;
        this.file = new File(fileLocation + fileName);
        this.type = BufferType.FREE;
    }

    protected BufferType getType(){
        return type;
    }

    protected void setType(BufferType type){
        this.type = type;
    }

    protected void clearBuffer(){
        this.blocks.clear();
        this.blockIndex = 0;
    }

    protected void prepareBuffer(String fileName){
        clearBuffer();
        this.file = new File(fileLocation + fileName);
    }

    protected long getNextBlock() throws BufferOverflowError{
        if(blockIndex >= BLOCK_SIZE) throw new BufferOverflowError(file.getName(), blockIndex);
        return blocks.get(blockIndex++);
    }

    protected boolean writeNextBlock(long block) throws BufferOverflowError{
        if(blockIndex >= BLOCK_SIZE) throw new BufferOverflowError(file.getName(), blockIndex);
        blocks.set(blockIndex++, block);
        return true;
    }

    protected int readFile() {
        if (!file.exists()) return -1;
        DataInputStream dataInputStream;
        try {
            dataInputStream = new DataInputStream(new FileInputStream(file));
            while (dataInputStream.available() > 0) {
                blocks.add(dataInputStream.readLong());
            }
            return blocks.size();
        } catch (IOException ignored) {
        }
        return -1;
    }

    protected void writeBuffer(){
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(new FileOutputStream(file));
            for(long block : blocks){
                dataOutputStream.writeLong(block);
            }
            dataOutputStream.close();
        } catch (IOException ignored) {
        }
    }

    protected boolean isBufferFull(){
        return blockIndex >= BLOCK_SIZE;
    }

}