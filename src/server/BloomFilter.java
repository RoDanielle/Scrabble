package server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.BitSet;


public class BloomFilter {
    private final BitSet bitset;
    private final MessageDigest[] hashFunctions;
    private final int Size;

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

    public void add(String s) {
        for (MessageDigest hash : hashFunctions) {
            bitset.set(getWordBit(s,hash));
        }
    }

    public boolean contains(String s) {
        for (MessageDigest hash : hashFunctions) {
            if (!bitset.get(getWordBit(s,hash))) {
                return false;
            }
        }
        return true;
    }
    public int getWordBit(String word, MessageDigest MD) {
        byte[] bts = MD.digest(word.getBytes());
        BigInteger bigInt = new BigInteger(bts);
        return Math.abs(bigInt.intValue()) % Size;
    }

    @Override
    public String toString()
    {
        StringBuilder biteString = new StringBuilder();
        for (int i =0;i<bitset.length();i++)
            biteString.append(bitset.get(i) ? "1" : "0");
        return biteString.toString();
    }

}