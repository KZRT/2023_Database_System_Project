package database.internal;

public class BufferOverflowError extends StackOverflowError{
    public BufferOverflowError(String file, int bufferIndex){
        super("Buffer Overflow at File " + fileName + "At " + fileIndex + "Using Buffer " + bufferIndex
        + "\n use boolean isBufferFull() for checking buffer full"
                + "\n use releaseCache() for releasing cache");
    }
}
