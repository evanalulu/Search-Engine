package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents an inverted index data structure that maps words to their
 * positions in documents. The index maintains counts of words in documents and
 * the positions of words in each document.
 */
public class InvertedIndex {
	/** A map that stores the count of words in each document. */
	private final TreeMap<String, Integer> wordCountMap;

	/** A nested map that stores the positions of words in each document. */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexMap;

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
		indexMap.computeIfAbsent(word, k -> new TreeMap<>()).computeIfAbsent(location, k -> new TreeSet<>()).add(position);
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
		TreeMap<String, TreeSet<Integer>> locationMap = indexMap.get(word);
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
		TreeMap<String, TreeSet<Integer>> locationMap = indexMap.get(word);
		if (locationMap != null) {
			TreeSet<Integer> positions = locationMap.get(location);
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

	/**
	 * Returns an unmodifiable view of the set of all words that are indexed.
	 *
	 * @return an unmodifiable set of all indexed words
	 */
	public Set<String> viewWords() {
		return Collections.unmodifiableSet(indexMap.keySet());
	}

	/**
	 * Returns an unmodifiable view of the set of locations (files) where a specific
	 * word is indexed.
	 *
	 * @param word the word to query
	 * @return an unmodifiable set of locations (files) where the word is indexed,
	 *   or an empty set if the word is not indexed
	 */
	public Set<String> viewLocations(String word) {
		TreeMap<String, TreeSet<Integer>> locationMap = indexMap.get(word);
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
	public Set<Integer> viewPositions(String word, String location) {
		TreeMap<String, TreeSet<Integer>> locationMap = indexMap.get(word);
		if (locationMap != null) {
			TreeSet<Integer> positions = locationMap.get(location);
			if (positions != null) {
				return Collections.unmodifiableSet(positions);
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Returns the number of occurrences of the specified word in the index map.
	 *
	 * @param word the word to count occurrences for
	 * @return the number of occurrences of the word in the index map
	 */
	public int numWords(String word) {
		TreeMap<String, TreeSet<Integer>> locationMap = indexMap.get(word);
		if (locationMap != null) {
			return locationMap.values().stream().mapToInt(TreeSet::size).sum();
		}
		return 0;
	}

	/**
	 * Returns the number of occurrences of the specified word in the given
	 * location.
	 *
	 * @param word the word to count occurrences for
	 * @param location the location to search for occurrences of the word
	 * @return the number of occurrences of the word in the given location
	 */
	public int numLocations(String word, String location) {
		TreeMap<String, TreeSet<Integer>> locationMap = indexMap.get(word);
		return locationMap.size();
	}

	/**
	 * Returns the number of occurrences of the specified word at the given location
	 * and position.
	 *
	 * @param word the word to count occurrences for
	 * @param location the location to search for occurrences of the word
	 * @param position the position to search for occurrences of the word within the
	 *   location
	 * @return the number of occurrences of the word at the given location and
	 *   position
	 */
	public int numPositions(String word, String location, Integer position) {
		TreeMap<String, TreeSet<Integer>> locationMap = indexMap.get(word);
		if (locationMap != null) {
			TreeSet<Integer> positions = locationMap.get(location);
			return positions.size();
		}
		return 0;
	}

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
	
	/* TODO 
	public ArrayList<IndexSearcher> partialSearch(Set<String> queries) {
		ArrayList<IndexSearcher> results = new ArrayList<>();
		Map<String, IndexSearcher> lookup = null;
		
		for (String query : queries) {
			for (var outerEntry : indexMap.tailMap(query).entrySet()) {
				if (outerEntry.getKey().startsWith(query)) {
					for (var innerEntry : outerEntry.getValue().entrySet()) {
						int matches = innerEntry.getValue().size();
						String location = innerEntry.getKey();
						
						if (lookup.containsKey(location)) {
							IndexSearcher current = lookup.get(location);
							current.addCount(matches);
						}
						else {
							create a new result
							add it to both the list and the lookup map
						}
					}
				}
				else break
			}
		}
		
		Collections.sort(results);
		return results;
	}
	
	exactSearch
	*/

	/**
	 * Represents a search result in the inverted index, including the count of
	 * matches, score, and document path.
	 */
	public class IndexSearcher implements Comparable<IndexSearcher> {

		// TODO Make private
		
		/** The count of matches. */
		public int count;

		/** The score of the search result. */
		public Double score;

		/** The path of the document containing the matches. */
		public String where; // TODO final

		/**
		 * Constructs an IndexSearcher object with the given parameters.
		 *
		 * @param count The count of matches.
		 * @param score The score of the search result.
		 * @param where The path of the document containing the matches.
		 */
		public IndexSearcher(int count, Double score, String where) {
			this.count = count;
			this.score = score;
			this.where = where;
		}

		/**
		 * Retrieves the count of matches.
		 *
		 * @return The count of matches.
		 */
		public int getCount() {
			return count;
		}

		/**
		 * Adds the specified value to the count of matches.
		 *
		 * @param count The value to add to the count of matches.
		 */
		public void addCount(int count) {
			this.count += count;
			// TODO this.score = (double) this.count / wordCountMap.get(this.where);
		}

		/**
		 * Sets the count of matches to the specified value.
		 *
		 * @param count The value to set as the count of matches.
		 */
		public void setCount(int count) { // TODO Remove
			this.count = count;
		}

		/**
		 * Retrieves the score of the search result.
		 *
		 * @return The score of the search result.
		 */
		public Double getScore() {
			return score;
		}

		/**
		 * Retrieves the path of the document containing the matches.
		 *
		 * @return The path of the document containing the matches.
		 */
		public String getWhere() {
			return where;
		}

		/**
		 * Sets the score of the search result.
		 *
		 * @param score The score to set.
		 */
		public void setScore(Double score) { // TODO Remove
			this.score = score;
		}

		/**
		 * Sets the path of the document containing the matches.
		 *
		 * @param where The path to set.
		 */
		public void setWhere(String where) { // TODO Remove
			this.where = where;
		}

		/**
		 * Compares this IndexSearcher with another IndexSearcher for sorting.
		 *
		 * @param other The IndexSearcher to compare with.
		 * @return A negative integer, zero, or a positive integer if this object is
		 *   less than, equal to, or greater than the specified object.
		 */
		@Override
		public int compareTo(IndexSearcher other) {
			int scoreComparison = Double.compare(other.getScore(), this.getScore());
			if (scoreComparison != 0) {
				return scoreComparison;
			}

			int countComparison = Integer.compare(other.getCount(), this.getCount());
			if (countComparison != 0) {
				return countComparison;
			}

			return this.getWhere().toString().compareToIgnoreCase(other.getWhere().toString());
		}

		/**
		 * Formats the given score to a string representation with eight decimal places.
		 *
		 * @param score the score to be formatted
		 * @return the formatted score as a string with eight decimal places
		 */
		private static String formatScore(Double score) {
			DecimalFormat FORMATTER = new DecimalFormat("0.00000000"); // TODO Make a static member

			String formattedScore = FORMATTER.format(score);
			return formattedScore;
		}

		/**
		 * Returns a string representation of the IndexSearcher object.
		 *
		 * @return A string representation of the IndexSearcher object.
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("{\n");
			builder.append("  \"count\": ").append(count).append(",\n");
			builder.append("  \"score\": ").append(formatScore(score)).append(",\n");
			builder.append("  \"where\": \"").append(where).append("\"\n");
			builder.append("}");
			return builder.toString();
		}

	}

}
