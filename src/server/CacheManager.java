package server;

import java.util.LinkedHashSet;

public class CacheManager{
    LinkedHashSet<String> cacheM; //this structure keeps the order of entry
    CacheReplacementPolicy MemPolicy; // policy - LFU or LRU
    int MaxSize;
    int currentSize;



    /**
     * The CacheManager function is responsible for managing the cache.
     * It will add a new object to the cache if it does not exist in it, and update its frequency.
     * If the object exists in the cache, then its frequency will be updated and moved to front of queue.
     * If there is no space left in CacheManager, then an element with lowest frequency will be removed from CacheManager

     *
     * @param size Set the maximum size of the cache
     * @param policy Determine which policy to use
     *
     * @docauthor Trelent
     */
    public CacheManager(int size, CacheReplacementPolicy policy)
    {
        MaxSize = size;
        currentSize = 0;
        cacheM = new LinkedHashSet<>();
        MemPolicy = policy;
    }



    /**
     * The query function receives a string and checks whether it is in the data structure
     *
     * @param word Check if the word is in the cache
     *
     * @return True if the word is in the data structure cacheM, false otherwise.
     *
     * @docauthor Trelent
     */
    public boolean query(String word) {
        return cacheM.contains(word);
    }



    /**
     * The add function, given a word, will update the crp and add the word to the cache.
     * if its size is greater than the maximum size, then we will remove the word chosen by the crp from it.
     *
     * @param word Add a word to the cache
     *
     * @docauthor Trelent
     */
    public void add(String word) {
        if(currentSize == MaxSize){
            String WordRemoved = MemPolicy.remove();
            MemPolicy.add(word);
            cacheM.remove(WordRemoved);
            cacheM.add(word);
        }
        else {
            MemPolicy.add(word);
            cacheM.add(word);
            currentSize++;
        }
    }
}
