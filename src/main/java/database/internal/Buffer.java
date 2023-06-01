package database.internal;
import java.io.*;
import java.util.ArrayList;

class Buffer {
    private final ArrayList<Long> blocks;
    private int blockIndex;
    private int blockSize;
    private File file;
    private BufferType type;
    private final static String fileLocation = "buffer/";
    public final static int BLOCK_SIZE = 4;

    protected Buffer(BufferType type, String fileName){
        this.blocks = new ArrayList<>(BLOCK_SIZE);
        this.blockIndex = 0;
        this.blockSize = 0;
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
        for (int i = 0; i < BLOCK_SIZE; i++) blocks.add(0L);
        this.blockIndex = 0;
        this.blockSize = 0;
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
        return isBufferFull();
    }

    protected int readFile() {
        if (!file.exists()) return -1;
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            byte[] temp = new byte[1024];
            int i = 0;
            while(bufferedInputStream.read(buffer) != -1){
                for(byte b : buffer){
                    temp[i++] = b;
                    if(b == '\n') {
                        blockSize++;
                        blocks.set(blockIndex++, Long.parseLong(new String(temp, 0, i).trim()));
                        i = 0;
                        temp = new byte[1024];
                    }
                }
            }
            bufferedInputStream.close();
            this.blockIndex = 0;
            return blockSize;
        } catch (IOException ignored) {
        }
        return -1;
    }

    protected void writeBuffer(){
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            for(int i = 0; i < blockIndex; i++){
                bufferedOutputStream.write((blocks.get(i) + "\n").getBytes());
            }
            bufferedOutputStream.close();
        } catch (IOException ignored) {
        }
    }

    protected boolean isBufferFull(){
        return blockIndex >= BLOCK_SIZE;
    }

    protected int getBlockIndex() {
        return blockIndex;
    }
}