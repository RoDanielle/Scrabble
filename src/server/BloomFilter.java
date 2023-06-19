package server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.BitSet;

public class BloomFilter {
    private final BitSet bitset;
    private final MessageDigest[] hashFunctions;
    private final int Size;



    /**
     * The BloomFilter function takes in a string and hashes it using the hash functions
     * that were passed into the constructor.

     *
     * @param size Initialize the size of the bitset
     * @param algs Parameter list of algorithm names
     *
     * @docauthor Trelent
     */
    public BloomFilter(int size, String... algs) {
        // Initialize the BitSet and hash function array
        bitset = new BitSet(size);
        hashFunctions = new MessageDigest[algs.length];
        int i = 0;
        for (String h : algs) {
            try {
                hashFunctions[i] = MessageDigest.getInstance(h);
                i++;
            } catch (Exception e) {
                System.err.println("The Hash name is not Valid");
            }
        }
        Size = size;
    }



    /**
     * The add function, given a string, inserts it into the bloom filter, that is:
     * Runs all the hash functions on the string
     * and turn on the relevant bits in the bit array.
     * uses a helper function getWordBit()

     *
     * @param s The string that is added to the bloom filter
     *
     * @docauthor Trelent
     */
    public void add(String s) {
        for (MessageDigest hash : hashFunctions)
            bitset.set(getWordBit(s,hash));
    }



    /**
     * The contains function checks to see if the given string is in the Bloom Filter.
     * uses a helper function getWordBit()

     *
     * @param s The string that is checked to see if it is in the bloom filter
     *
     * @return True if the word is in the filter, false otherwise
     *
     * @docauthor Trelent
     */
    public boolean contains(String s) {
        for (MessageDigest hash : hashFunctions)
            if (!bitset.get(getWordBit(s,hash)))
                return false;
        return true;
    }



    /**
     * The getWordBit function takes a word and a MessageDigest object as input.
     * It then uses the MessageDigest object to hash the word into an array of bytes,
     * which it converts into a BigInteger.  The getWordBit function returns the absolute value of this BigInteger modulo Size,
     * where Size is defined in BloomFilter.java as 2^32 - 1 (the maximum size for an int).
     * This ensures that getWordBit will always return an integer between 0 and 2^32 - 1 inclusive.

     *
     * @param word Get the bytes of the word and then convert it to a biginteger
     * @param MD Hash the word and return a byte array
     *
     * @return The bit position of the word
     *
     * @docauthor Trelent
     */
    public int getWordBit(String word, MessageDigest MD) {
        byte[] bts = MD.digest(word.getBytes());
        BigInteger bigInt = new BigInteger(bts);
        return Math.abs(bigInt.intValue()) % Size;
    }



    /**
     * The toString function returns a string representation of the bitset.

     *
     * @return A string of 1's and 0's, which is the binary representation of the bitset
     *
     * @docauthor Trelent
     */
    @Override
    public String toString() {
        StringBuilder biteString = new StringBuilder();
        for (int i =0;i<bitset.length();i++)
            biteString.append(bitset.get(i) ? "1" : "0");
        return biteString.toString();
    }
}