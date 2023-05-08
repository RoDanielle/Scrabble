package test;
import java.util.LinkedHashSet;

public class LRU implements CacheReplacementPolicy {
    LinkedHashSet<String> words; //this structure keeps the order of entry

    public LRU(){ //constructor
        words = new LinkedHashSet<String>();
    }

    @Override
    public void add(String word){ //Updating the time of use of the word
        words.remove(word); //remove default
        words.add(word);
    }

    @Override
    public String remove() {
        String word = words.iterator().next();
        words.remove(word);
        return word;
    }
}