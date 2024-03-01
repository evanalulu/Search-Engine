package edu.usfca.cs272;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class InvertedIndex {
	// TODO Make both of these final

	private TreeMap<String, Integer> wordCountMap;

    private TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap;

    public InvertedIndex() {
        wordCountMap = new TreeMap<>();
        indexMap = new TreeMap<>();
    }

    public void addCount(String location, Integer count) {
    	// TODO if count > 0
        wordCountMap.put(location, count);
    }

    public void addWord(String word, String location, Integer position) {
        indexMap.putIfAbsent(word, new TreeMap<>());
        TreeMap<String, ArrayList<Integer>> locationMap = indexMap.get(word);
        locationMap.putIfAbsent(location, new ArrayList<>());
        ArrayList<Integer> positions = locationMap.get(location);
        positions.add(position);
    }

    // Getters
    public Map<String, Integer> getWordCountMap() {
        return wordCountMap;
    }

    public TreeMap<String, TreeMap<String, ArrayList<Integer>>> getIndexMap() {
        return indexMap;
    }
    
    /*
     * TODO Add more generally useful data structure methods
     * 
     * Start trying to create safer get methods
     */
}
