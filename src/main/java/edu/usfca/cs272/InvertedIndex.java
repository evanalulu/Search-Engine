package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
	// TODO private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexMap;

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

	/**
	 * Writes the word count map to a JSON file specified by the given output path.
	 *
	 * @param output the path to the output JSON file
	 * @throws IOException if an I/O error occurs while writing the JSON file
	 */
	public void writeWordCountMap(Path output) throws IOException {
		JsonWriter.writeObject(wordCountMap, output);
	}

	/**
	 * Writes the index map to a JSON file specified by the given output path.
	 *
	 * @param output the path to the output JSON file
	 * @throws IOException if an I/O error occurs while writing the JSON file
	 */
	public void writeIndexMap(Path output) throws IOException {
		JsonWriter.writeWordPositionsMap(indexMap, output);
	}

	/**
	 * Returns the count of words for a given document.
	 *
	 * @param path the document to query
	 * @return the count of words in the document, or 0 if the document is not
	 *   indexed
	 */
	public int getWordCount(String path) {
		return wordCountMap.getOrDefault(path, 0);
	}

	/**
	 * Checks if a specific document is indexed in word count map.
	 *
	 * @param path the document to check
	 * @return {@code true} if the document is indexed
	 */
	public boolean hasFileinCount(String path) {
		return wordCountMap.containsKey(path);
	}

	/**
	 * Returns the number of documents indexed.
	 *
	 * @return the number of indexed documents
	 */
	public int getFileCount() {
		return wordCountMap.size();
	}

	/**
	 * Checks if a specific word is indexed in any document.
	 *
	 * @param word the word to check
	 * @return {@code true} if the word is indexed
	 */
	public boolean hasWord(String word) {
		return indexMap.containsKey(word);
	}

	/**
	 * Checks if a specific location (file) is indexed for a given word.
	 *
	 * @param word the word to check
	 * @param location the location (file) to check
	 * @return {@code true} if the location is indexed for the specified word
	 */
	public boolean hasLocation(String word, String location) {
		TreeMap<String, ArrayList<Integer>> locationMap = indexMap.get(word);
		return locationMap != null && locationMap.containsKey(location);
	}

	/**
	 * Checks if a specific position for a word is indexed in a given location
	 * (file).
	 *
	 * @param word the word to check
	 * @param location the location (file) to check
	 * @param position the position of the word to check
	 * @return {@code true} if the position is indexed for the word in the location
	 */
	public boolean hasPosition(String word, String location, Integer position) {
		TreeMap<String, ArrayList<Integer>> locationMap = indexMap.get(word);
		if (locationMap != null) {
			ArrayList<Integer> positions = locationMap.get(location);
			return positions != null && positions.contains(position);
		}
		return false;
	}

	/**
	 * Returns an unmodifiable view of the set of all files (locations) indexed.
	 *
	 * @return an unmodifiable set of all indexed files
	 */
	public Set<String> viewFiles() {
		return Collections.unmodifiableSet(wordCountMap.keySet());
	}
	
	// TODO viewWords() keyset of the indexmap

	/**
	 * Returns an unmodifiable view of the set of locations (files) where a specific
	 * word is indexed.
	 *
	 * @param word the word to query
	 * @return an unmodifiable set of locations (files) where the word is indexed,
	 *   or an empty set if the word is not indexed
	 */
	public Set<String> viewLocations(String word) {
		TreeMap<String, ArrayList<Integer>> locationMap = indexMap.get(word);
		if (locationMap != null) {
			return Collections.unmodifiableSet(locationMap.keySet());
		}
		return Collections.emptySet();
	}

	/**
	 * Returns an unmodifiable list of positions for a specific word in a specific
	 * file.
	 *
	 * @param word the word to query
	 * @param location the file to query
	 * @return an unmodifiable list of positions for the word in the specified file,
	 *   or an empty list if the word or file is not indexed
	 */
	public List<Integer> viewPositions(String word, String location) {
		TreeMap<String, ArrayList<Integer>> locationMap = indexMap.get(word);
		if (locationMap != null) {
			ArrayList<Integer> positions = locationMap.get(location);
			if (positions != null) {
				return Collections.unmodifiableList(new ArrayList<>(positions));
				// TODO return Collections.unmodifiableList(positions);
			}
		}
		return Collections.emptyList();
	}
	
	/* TODO 
	numWords, numLocations, etc.
	copy all the has methods and change the contains to size
	*/

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Word Count Map:\n");
		wordCountMap
				.forEach((file, count) -> { builder.append("  ").append(file).append(": ").append(count).append("\n"); });

		builder.append("\nIndex Map:\n");
		indexMap.forEach((word, fileMap) -> {
			builder.append("  ").append(word).append(":\n");
			fileMap.forEach((file, positions) -> {
				builder.append("    ").append(file).append(": ").append(positions).append("\n");
			});
		});

		return builder.toString();
	}
}
