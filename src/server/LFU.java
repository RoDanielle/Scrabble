package server;

import java.util.LinkedHashMap;

public class LFU implements CacheReplacementPolicy{
    LinkedHashMap<String,Integer> words; // string = key(k) integer = value(v)



    /**
     * The constructor for the LFU class which is the implementation of the CacheReplacementPolicy interface
     * Which defines the policy of the replacement of words in the cache
     * Its policy is: least frequently used
     *
     */
    public LFU(){
        words = new LinkedHashMap<String,Integer>();
    }



    /**
     * The add function adds a word to the structure while maintaining its policy.
     * If the word already exists, it's counter is incremented by 1.
     *
     * @param word The word you want to add to the structure.
     *
     */
    @Override
    public void add(String word) {
        Integer valAdd = words.remove(word);
        if (valAdd != null) //if the word exists counter++
            words.put(word,valAdd+1);
        else words.put(word,1); //if the word doesn't exist, and it's counter is 1, it's added to the structure
    }



    /**
     * The remove function removes the least frequently used word from the cache.
     * In case of a tie, the one that entered first is returned
     *
     * @return The key of the least frequent word
     *
     */
    @Override
    public String remove() {
        String[] temp = new String[2];
        words.forEach((key,val) ->{
            if(temp[0] == null) {
                temp[0] = key;
                temp[1] = String.valueOf(val);
            }
            else {
                if(Integer.parseInt(temp[1])> val){
                    temp[0] = key;
                    temp[1] = String.valueOf(val);
                }
            }
        });
        words.remove(temp[0]);
        return temp[0];
    }
}