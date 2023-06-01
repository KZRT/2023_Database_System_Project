package database.internal;

public class BufferOverflowError extends StackOverflowError{
    public BufferOverflowError(String file, int blockIndex){
        super("Buffer Overflow at File " + file + "At " + blockIndex
        + "\n use boolean isBufferFull() for checking buffer full"
                + "\n use releaseCache() for releasing cache");
    }
}
