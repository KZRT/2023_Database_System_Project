package database.internal;

import java.io.IOException;
import java.util.ArrayList;

public class Cache {
    private final ArrayList<Buffer> buffers;
    private final ArrayList<Integer> lruIndex;
    private int fileIndex;
    private int bufferIndex;
    private static final int CACHE_SIZE = 4;
    private static Cache instance;

    private Cache(){
        this.buffers = new ArrayList<>(CACHE_SIZE);
        for(int i = 0; i < CACHE_SIZE; i++) buffers.add(new Buffer(BufferType.FREE, ""));
        this.lruIndex = new ArrayList<>(CACHE_SIZE);
        this.fileIndex = 0;
        this.bufferIndex = 0;
    }

    public boolean isBufferAvailable(int count) {
        if (countAvailableBuffer() >= count) return true;
        else {
            dumpCache();
            return countAvailableBuffer() >= count;
        }
    }

    private int countAvailableBuffer(){
        int count = 0;
        for(Buffer buffer : buffers){
            if(buffer.getType() == BufferType.FREE) count++;
        }
        return count;
    }

    private void dumpCache(){
        for(Buffer buffer : buffers){
            if(buffer.getType() == BufferType.WRITE){
                buffer.writeBuffer();
                buffer.clearBuffer();
                buffer.setType(BufferType.FREE);
            }
        }
    }

    public void releaseCache(){
        dumpCache();
        fileIndex = 0;
        bufferIndex = 0;
        for(Buffer buffer : buffers){
            buffer.clearBuffer();
            buffer.setType(BufferType.FREE);
        }
    }

    public int acquireBuffer(String fileName){
        int cycleTime = 0;
        while (true){
            if(bufferIndex >= CACHE_SIZE) bufferIndex = 0;
            if(buffers.get(bufferIndex).getType() == BufferType.FREE){
                buffers.get(bufferIndex).prepareBuffer(fileName + fileIndex);
                buffers.get(bufferIndex).setType(BufferType.PINNED);
                return bufferIndex;
            }
            bufferIndex++;
            cycleTime++;
            if(cycleTime >= CACHE_SIZE) throw new BufferPinnedError(fileName, fileIndex, bufferIndex);
        }
    }

    public boolean isBufferFull(int bufferIndex){
        return buffers.get(bufferIndex).isBufferFull();
    }


    public void releaseBuffer(int bufferIndex, boolean write){
        if(write) buffers.get(bufferIndex).setType(BufferType.WRITE);
        else buffers.get(bufferIndex).setType(BufferType.FREE);
    }

    public int readBuffer(int bufferIndex){
        return buffers.get(bufferIndex).readFile();
    }

    public long getNextBlock(int bufferIndex) throws BufferOverflowError{
        return buffers.get(bufferIndex).getNextBlock();
    }

    public boolean writeBlockToBuffer(int bufferIndex, long block){
        return buffers.get(bufferIndex).writeNextBlock(block);
    }

    public void increaseIndex(int fileIndex){
        this.fileIndex += fileIndex;
    }

    public static Cache getInstance(){
        if(instance == null){
            instance = new Cache();
        }
        return instance;
    }

    public int getIndex(int bufferIndex){
        return fileIndex * Buffer.BLOCK_SIZE + buffers.get(bufferIndex).getBlockIndex() - 1;
    }

    public boolean isNextBlockNotAvailable(int bufferIndex){
        return buffers.get(bufferIndex).isBufferFull();
    }
}
