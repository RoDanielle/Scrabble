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



    /**
     * The Dictionary function receives a parameter list of file names that constitute stories and inserts all the words from those files into a bloom filter.
     *
     * @param files Parameter list of file names that constitute stories
     *
     */
    public Dictionary(String...files){
        fileNames = files;
        LRU_CM = new CacheManager(400, new LRU());
        LFU_CM = new CacheManager(100, new LFU());
        //Bloom = new BloomFilter(262144,"MD5","SHA1");
        Bloom = new BloomFilter(262144,"MD2","MD5","SHA1","SHA-256","SHA-512");
        for(int i = 0; i < files.length; i++) // adds words from files to bloomfilter
            loadFile(files[i]);
    }



    /**
     * The loadFile function takes a file name as an argument and loads the words from that file into the bloom filter.
     *
     * @param fileName Specify the file that will be read from
     * @throws RuntimeException if the stream is failed
     *
     */
    public void loadFile(String fileName) {
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



    /**
     * The query function takes a word as input and checks if it is in the dictionary.
     * Search in the cache manager for the existing words (LRU) if found we will return true, otherwise:
     * Search in the cache manager for the words that do not exist (LFU) if found we will return false, otherwise:
     * Search in BloomFilter, and we will return his answer after we update the appropriate cache manager with the answer.
     * Word found, adding to LRU
     * Word not found, adding to LFU
     *
     * @param word Check if the word is in the cache
     *
     * @return True if the word is found, false otherwise
     *
     */
    public boolean query(String word){
        if(LRU_CM.query(word))
            return true;
        if(LFU_CM.query(word))
            return false;
        boolean CheckBloomFilter = Bloom.contains(word);
        if(CheckBloomFilter)
            LRU_CM.add(word);
        else
            LFU_CM.add(word);
        return CheckBloomFilter;
    }



    /**
     * The challenge function receives a word and checks if it is in the dictionary in the inventions of the IOSearch session and will return its answer.
     * The function will update the corresponding cache manager with the answer.
     * If the word is found, the word will be added to the LRU cache.
     * If the word is not found, then the word will be added to the LFU cache.
     *
     * @param word Search the word in the filenames array
     *
     * @return True if the word is found, false otherwise
     *
     */
    public boolean challenge(String word){
        boolean searchTest;
        try{
            searchTest = IOSearcher.search(word, fileNames);
        } catch(RuntimeException e){
            return false;
        }
        if(searchTest)
            LRU_CM.add(word);
        else
            LFU_CM.add(word);
        return searchTest;
    }
}


