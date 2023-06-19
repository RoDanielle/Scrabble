package server;

import java.util.LinkedHashSet;

public class LRU implements CacheReplacementPolicy {
    LinkedHashSet<String> words; //this structure keeps the order of entry



    /**
     * The constructor for the LRU class which is the implementation of the CacheReplacementPolicy interface
     * Which defines the policy of the replacement of words in the cache
     * Its policy is: least recently used

     *
     * @docauthor Trelent
     */
    public LRU(){
        words = new LinkedHashSet<String>();
    }



    /**
     * The add function adds a word to the list of words while maintaining its policy.

     *
     * @param word Add the word to the list of words
     *
     * @docauthor Trelent
     */
    @Override
    public void add(String word){ //Updating the time of use of the word
        words.remove(word); //remove default
        words.add(word);
    }



    /**
     * The remove function removes the least recently used word

     *
     * @return the least recently used word
     *
     * @docauthor Trelent
     */
    @Override
    public String remove() {
        String word = words.iterator().next();
        words.remove(word);
        return word;
    }
}