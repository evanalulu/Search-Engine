package edu.usfca.cs272;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an inverted index data structure that maps words to their
 * positions in documents. The index maintains counts of words in documents and
 * the positions of words in each document.
 */
public class InvertedIndex {
	/** A map that stores the count of words in each document. */
	private final TreeMap<String, Integer> wordCountMap;

	/** A nested map that stores the positions of words in each document. */
	private final TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap;

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
<<<<<<< HEAD
		if (count > 0) {
=======
		// TODO Due to how bug prone it is, most modern code style guides disallow 1 line if/else statements without { } curly braces
		if (count > 0)
>>>>>>> b8730d64802f31de3f3f0634c2fc0da0fd66abba
			wordCountMap.put(location, count);
		}
	}

	/**
	 * Adds the position of a word in a document to the index map.
	 *
	 * @param word the word to add
	 * @param location the path of the document
	 * @param position the position of the word in the document
	 */
	public void addWord(String word, String location, Integer position) {
<<<<<<< HEAD
		indexMap.computeIfAbsent(word, k -> {
			TreeMap<String, ArrayList<Integer>> locationMap = new TreeMap<>();
			locationMap.put(location, new ArrayList<>());
			return locationMap;
		}).computeIfAbsent(location, k -> new ArrayList<>()).add(position);
=======
		/*
		 * TODO Time to refactor this add method. It can be either more compact, more
		 * efficient, or both:
		 * 
		 * 1. Focus on making the most compact code possible with a 3 line solution and
		 * putIfAbsent, but extra inefficient get calls. 2. Focus on making the most
		 * efficient code possible by reducing the number of times the underlying data
		 * is accessed (without using putIfAbsent or containsKey methods, always get and
		 * check for null instead). 3. Focus on making the most compact and efficient
		 * code by using lambda expressions and the computeIfAbsent method.
		 * 
		 * Choose one option, then make the same design choice in all your other
		 * methods! Some example links are below for #1 and #2.
		 */

// 1. Visit: https://github.com/usf-cs272-spring2024/cs272-lectures/blob/b58d2cfc1f26c8916ddcb9261bc1143e29923e6d/src/main/java/edu/usfca/cs272/lectures/inheritance/word/WordLength.java#L40-L41
// 2. Visit: https://github.com/usf-cs272-spring2024/cs272-lectures/blob/b58d2cfc1f26c8916ddcb9261bc1143e29923e6d/src/main/java/edu/usfca/cs272/lectures/inheritance/word/WordPrefix.java#L79-L86

		indexMap.putIfAbsent(word, new TreeMap<>());
		TreeMap<String, ArrayList<Integer>> locationMap = indexMap.get(word);
		locationMap.putIfAbsent(location, new ArrayList<>());
		ArrayList<Integer> positions = locationMap.get(location);
		positions.add(position);
>>>>>>> b8730d64802f31de3f3f0634c2fc0da0fd66abba
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
