package server;

import java.util.LinkedHashSet;

public class CacheManager{
    LinkedHashSet<String> cacheM; //this structure keeps the order of entry
    CacheReplacementPolicy MemPolicy; // policy - LFU or LRU
    int MaxSize;
    int currentSize;

    public CacheManager(int size, CacheReplacementPolicy policy)//constructor
    {
        MaxSize = size;
        currentSize = 0;
        cacheM = new LinkedHashSet<>();
        MemPolicy = policy;
    }

    public boolean query(String word)
    {
        return cacheM.contains(word);
    }

    public void add(String word)
    {
        if(currentSize == MaxSize){
            String WordRemoved = MemPolicy.remove();
            MemPolicy.add(word);
            cacheM.remove(WordRemoved);
            cacheM.add(word);
        }
        else
        {
            MemPolicy.add(word);
            cacheM.add(word);
            currentSize++;
        }
    }
}
