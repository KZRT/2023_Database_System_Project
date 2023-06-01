package database.internal;

public class BufferPinnedError extends OutOfMemoryError{
    public BufferPinnedError(String fileName, int fileIndex, int bufferIndex){
        super("All buffers are pinned at File " + fileName + "At " + fileIndex + "Using Buffer " + bufferIndex
        + "\n use isAvailableBuffer() for checking available buffer"
                + "\n use releaseCache() for releasing cache");
    }
}
