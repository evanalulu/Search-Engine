package edu.usfca.cs272;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an inverted index data structure that maps words to their positions in documents.
 * The index maintains counts of words in documents and the positions of words in each document.
 */
public class InvertedIndex {
	
    /** A map that stores the count of words in each document. */
	private TreeMap<String, Integer> wordCountMap;

    /** A nested map that stores the positions of words in each document. */
    private TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap;

    /**
     * Constructs a new InvertedIndex with empty word count and index maps.
     */
    public InvertedIndex() {
        wordCountMap = new TreeMap<>();
        indexMap = new TreeMap<>();
    }
    
    /**
     * Adds the count of a word in a document to the word count map.
     *
     * @param location the path of the document
     * @param count the count of the word in the document
     */
    public void addCount(String location, Integer count) {
        wordCountMap.put(location, count);
    }

    /**
     * Adds the position of a word in a document to the index map.
     *
     * @param word the word to add
     * @param location the path of the document
     * @param position the position of the word in the document
     */
    public void addWord(String word, String location, Integer position) {
        indexMap.putIfAbsent(word, new TreeMap<>());
        TreeMap<String, ArrayList<Integer>> locationMap = indexMap.get(word);
        locationMap.putIfAbsent(location, new ArrayList<>());
        ArrayList<Integer> positions = locationMap.get(location);
        positions.add(position);
    }

    /**
     * Returns the word count map.
     *
     * @return the word count map
     */    
    public Map<String, Integer> getWordCountMap() {
        return wordCountMap;
    }

     /**
      * Returns the index map.
      *
      * @return the index map
      */
    public TreeMap<String, TreeMap<String, ArrayList<Integer>>> getIndexMap() {
        return indexMap;
    }
}
