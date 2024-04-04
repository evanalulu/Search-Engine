package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
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

	/*
	 * TODO Unless a file is being opened or written, none of the methods below
	 * should take Path parameters. What happens when we move to storing URLs
	 * instead for web pages? Would we need to convert that String to a Path just so
	 * we can check if it exists in the index?
	 */
	
	/**
	 * Returns the count of words for a given document.
	 *
	 * @param path the document to query
	 * @return the count of words in the document, or 0 if the document is not
	 *   indexed
	 */
	public int getWordCount(Path path) {
		return wordCountMap.getOrDefault(path.toString(), 0);
	}

	/**
	 * Checks if a specific document is indexed in word count map.
	 *
	 * @param path the document to check
	 * @return true if the document is indexed, false otherwise
	 */
	public boolean hasFileinCount(Path path) {
		return wordCountMap.containsKey(path.toString());
	}
	
	// TODO Move the getFileCount() method here since it also accesses wordCountMap?
	
	/**
	 * Checks if a specific word is indexed in any document.
	 *
	 * @param word the word to check
	 * @return true if the word is indexed, false otherwise
	 */
	public boolean hasWord(String word) {
		return indexMap.containsKey(word);
	}

	/**
	 * Checks if the specified file is indexed in any of the word entries in index
	 * map.
	 *
	 * @param file the file to check
	 * @return true if the file is indexed, false otherwise
	 */
	public boolean hasFileinIndex(String file) {
		/*
		 * TODO Want to do this without requiring any kind of looping. At this stage, we
		 * are only concerned about making the existing data safely and efficiently
		 * accessible. (Notice none of the lecture examples of this included this kind
		 * of streaming or looping.) Try instead:
		 * 
		 * public boolean hasFileForWord(String word, String file) { safely and efficiently check if indexMap.get(word) contains the file provided
		 * 
		 * ...similar to this:
		 * 
		 * https://github.com/usf-cs272-spring2024/cs272-lectures/blob/f9364c0fc5ea6e778366628ceda09f641c0f52b2/src/main/java/edu/usfca/cs272/lectures/inheritance/word/WordPrefix.java#L125-L126
		 */
		return indexMap.values().stream().anyMatch(locationMap -> locationMap.containsKey(file));
	}
	
	// TODO Missing a hasPositionForWordAndLocation ... or if you want a less wordy naming scheme, you need a hasWord(String word) hasLocation(String word, STring location) hasPosition(String word, String location, Integer position) 

	/**
	 * Returns the number of documents indexed.
	 *
	 * @return the number of indexed documents
	 */
	public int getFileCount() {
		return wordCountMap.size();
	}

	/**
	 * Returns an unmodifiable view of the set of all files (locations) indexed.
	 *
	 * @return an unmodifiable set of all indexed files
	 */
	public Set<String> viewFiles() {
		return Collections.unmodifiableSet(wordCountMap.keySet());
	}

	/**
	 * Returns an unmodifiable view of the set of words indexed in a specific file.
	 *
	 * @param file the file to query
	 * @return an unmodifiable set of words for the specified file, or an empty set
	 *   if the file is not indexed
	 */
	public Set<String> viewWordsInFile(String file) { // TODO Need a viewLocations(String word) instead
		Set<String> words = new HashSet<>();
		indexMap.forEach((word, locationMap) -> {
			if (locationMap.containsKey(file)) {
				words.add(word);
			}
		});
		return Collections.unmodifiableSet(words);
	}

	/**
	 * Returns an unmodifiable map of words and their positions in a specific file.
	 *
	 * @param file the file to query
	 * @return an unmodifiable map of words to their positions list for the
	 *   specified file, or an empty map if the file is not indexed
	 */
	public Map<String, ArrayList<Integer>> viewWordPositionsInFile(String file) { // TODO Need a viewPositions(String word, String location) instead
		Map<String, ArrayList<Integer>> wordPositions = new TreeMap<>();
		indexMap.forEach((word, locationMap) -> {
			ArrayList<Integer> positions = locationMap.get(file);
			if (positions != null) {
				wordPositions.put(word, new ArrayList<>(positions));
			}
		});
		return Collections.unmodifiableMap(wordPositions);
	}
	
	// TODO How about a toString?
}
