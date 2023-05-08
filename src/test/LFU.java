package test;

//import java.util.Collections;
//import java.util.Comparator;
import java.util.LinkedHashMap;

public class LFU implements CacheReplacementPolicy{
    LinkedHashMap<String,Integer> words; // string = key(k) integer = value(v)
    public LFU(){ //constructor
        words = new LinkedHashMap<String,Integer>();
    }


    @Override
    public void add(String word) {
        Integer valAdd = words.remove(word);
        if (valAdd != null) //if the word exists counter++
            words.put(word,valAdd+1);
        else words.put(word,1); //if the word doesn't exist and it's counter is 1, it's added to the structure 
    }

    @Override
    public String remove() {
        String[] temp = new String[2];
        words.forEach((key,val) ->{
            if(temp[0] == null)
            {
                temp[0] = key;
                temp[1] = String.valueOf(val);
            }
            else 
            {
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