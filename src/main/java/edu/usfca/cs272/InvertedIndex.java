package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
		if (count > 0) {
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
		indexMap.computeIfAbsent(word, k -> new TreeMap<>())
				.computeIfAbsent(location, k -> new ArrayList<>())
				.add(position);
	}

	/*
	 * TODO The get methods here are breaking encapsulation. It is now time to fix
	 * this problem. See lecture examples linked below for how to fix this problem
	 * efficiently.
	 */

	// TODO PrefixMap/PrefixDemo:
	// https://usf-cs272-spring2024.notion.site/PrefixMap-and-Demo-2862dfab600341c8bfb20da697def0ce
	// TODO WordGroup/WordPrefix:
	// https://usf-cs272-spring2024.notion.site/WordGroupDemo-82c94c5b10c841a1a2e0df2ba1ecab37

	/**
	 * Returns the word count map.
	 * 
	 * @param output the count output path
	 *
	 * @throws IOException
	 */
	public void writeWordCountMap(Path output) throws IOException {
		JsonWriter.writeObject(wordCountMap, output);
	}

	/**
	 * Returns the index map.
	 * 
	 * @param output the index output path
	 *
	 * @throws IOException
	 */
	public void writeIndexMap(Path output) throws IOException {
		JsonWriter.writeWordPositionsMap(indexMap, output);
	}

	/*
	 * TODO This class is still missing some methods. Use some of the class
	 * (WordGroup/WordPrefix) and homework (FileIndex) examples to guide what you
	 * need to add here. Some are linked below. Focus on (1) the type of methods
	 * those examples have (add, get/view, num/size, etc.) and (2) how many. For
	 * example, FileIndex has two "has" methods because there are two pieces of
	 * information stored within that data structure class (the locations and the
	 * words for a location). What does that mean for this class, which is storing
	 * more information?
	 */

}
