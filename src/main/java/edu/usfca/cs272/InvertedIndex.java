package edu.usfca.cs272;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class InvertedIndex {

	private Map<String, Integer> wordCountMap;

    private TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap;

    public InvertedIndex() {
        wordCountMap = new HashMap<>();
        indexMap = new TreeMap<>();
    }

    public void addCount(String location, Integer count) {
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
}
