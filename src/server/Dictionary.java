package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Dictionary {

    String[] fileNames;
    CacheManager LRU_CM;
    CacheManager LFU_CM;
    BloomFilter Bloom;
    public Dictionary(String...files){ //constructor
        fileNames = files;
        LRU_CM = new CacheManager(400, new LRU());
        LFU_CM = new CacheManager(100, new LFU());
        Bloom = new BloomFilter(1048576,"MD5","SHA1");
        for(int i = 0; i < files.length; i++) // adds words from files to bloomfilter
    	{
            loadFile(files[i]);
    	}
        for(String s : fileNames)
        {
            System.out.println("files name from dictionary: " + s.toString());
        }
    }
    
    public void loadFile(String fileName) { // loads words from a file in order to feed it to bloomfilter 
        try{
            Stream<String> stringStream = Files.lines(Paths.get(fileName)); // takes a line from the file
            stringStream.forEach(line->{
                Stream.of(line.split("\\s+")).forEach(word->Bloom.add(word)); // extracts to words for a line (by space char)
            });
            stringStream.close();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public boolean query(String word){
        if(LRU_CM.query(word)){ // search in LRU (existing word)
            System.out.println("lru");
            return true;
        }
        if(LFU_CM.query(word)){ // search in LFU (none existing word)
            System.out.println("lfu");
            return false;
        }
        boolean CheckbloomFilter = Bloom.contains(word);
        System.out.println("bloom filter dictionary - " + CheckbloomFilter);
        if(CheckbloomFilter){

        	LRU_CM.add(word); // word found, adding to LRU
        }
        else{
        	LFU_CM.add(word);// word not found, adding to LFU
        }
        return CheckbloomFilter;
    }

    public boolean challenge(String word){
        boolean searchTest;
        try{
            searchTest = IOSearcher.search(word, fileNames);
        }
        catch(RuntimeException e){
            return false;
        }
        if(searchTest){
        	LRU_CM.add(word);
        }
        else{
        	LFU_CM.add(word);
        }
        return searchTest;
    }
}


