package database.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Cache {
    private final ArrayList<Buffer> buffers;
    private final ArrayList<Integer> lruIndex;
    private int pinnedIndex = -1;
    private int fileIndex = 0;
    private static final int CACHE_SIZE = 4;
    private static Cache instance;

    private Cache(){
        this.buffers = new ArrayList<>(CACHE_SIZE);
        this.lruIndex = new ArrayList<>(CACHE_SIZE);
    }

    private void updateLRU(int index){
        lruIndex.replaceAll(i -> i + 1);
        lruIndex.set(index, 0);
    }

    private int getLRUIndex(){
        ArrayList<Integer> temp = new ArrayList<>();
        for(int i = 0; i < CACHE_SIZE; i++){
            if(buffers.get(i).getType() != BufferType.PINNED) temp.add(i);
        }
        int max = 0;
        for(int i : temp) if(lruIndex.get(i) > max) max = lruIndex.get(i);
        return lruIndex.indexOf(max);
    }


    public Buffer getBuffer(int index){
        updateLRU(index);
        return buffers.get(index);
    }

    public void setBuffer(int index, Buffer buffer){
        buffers.set(index, buffer);
    }

    public boolean isFull(){
        for (Buffer buffer : buffers) {
            if (!buffer.isFull()) return false;
        }
        return true;
    }


    public boolean writeBlock(long block){
        if(pinnedIndex < 0) pinnedIndex = getLRUIndex();
        if(buffers.get(pinnedIndex).isFull()) {
            buffers.get(pinnedIndex).setType(BufferType.WRITE);
        } else {
            buffers.get(pinnedIndex).getBlocks().add(block);
        }
        return false;
    }

    public void clearCache(){
        buffers.clear();
        lruIndex.clear();
        fileIndex = 0;
    }


    public static Cache getInstance(){
        if(instance == null){
            instance = new Cache();
        }
        return instance;
    }


    private boolean resizeCache(){
        if(isFull()){
            for(int i = 0; i < CACHE_SIZE; i++){
                int index = getLRUIndex();
                Buffer buffer = buffers.get(index);
                switch (buffer.getType()) {
                    case FREE -> {
                        return true;
                    }
                    case WRITE -> {
                        try {
                            buffer.writeFullBuffer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        buffer.setType(BufferType.FREE);
                        return true;
                    }
                    case PINNED -> {
                        continue;
                    }
                }
                buffers.set(index, null);
                lruIndex.set(index, null);
                return true;
            }
        } else return true;

        return false;
    }
}
