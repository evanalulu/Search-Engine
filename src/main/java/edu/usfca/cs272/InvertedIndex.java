package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

	/*
	 * TODO public void ArrayList<IndexSearcher> exactSearch(Set<String> query) {
	 * ArrayList<IndexSearcher> outerList = new ArrayList<>();
	 * 
	 * for (String queryTerm : query) { ArrayList<IndexSearcher> innerList = new
	 * ArrayList<>();
	 * 
	 * if (index.hasWord(queryTerm)) { Set<String> locations =
	 * index.viewLocations(queryTerm);
	 * 
	 * for (String path : locations) { Set<Integer> value =
	 * index.viewPositions(queryTerm, path); calculateResult(result, queryString,
	 * index, path, value); } }
	 * 
	 * outerList.addAll(innerList); }
	 * 
	 * Collections.sort(outerList); }
	 * 
	 * ...and partial search (and all of the helper methods needed)
	 */

	/**
	 * Performs exact search based on the provided query, updating the result map
	 * with search results.
	 *
	 * @param query The query terms to search for.
	 * @param index The inverted index to search within.
	 * @param result The map to store the search results, where each query term maps
	 *   to a list of IndexSearchers.
	 */
	public static void exactSearch(TreeSet<String> query, InvertedIndex index,
			TreeMap<String, ArrayList<IndexSearcher>> result) {

		for (String queryTerm : query) {
			String queryString = treeSetToString(query);
			ArrayList<IndexSearcher> innerList = new ArrayList<>();

			if (index.hasWord(queryTerm)) {
				Set<String> locations = index.viewLocations(queryTerm);

				for (String path : locations) {
					Set<Integer> value = index.viewPositions(queryTerm, path);
					calculateResult(result, queryString, index, path, value);
				}
			}

			result.computeIfAbsent(queryString, k -> new ArrayList<>()).addAll(innerList);
			Collections.sort(result.get(queryString));
		}
	}

	/**
	 * Calculates and updates the search result based on the query string, inverted
	 * index, path, and set of matching positions. Updates the provided result map
	 * with the calculated information.
	 *
	 * @param result the map to store the search results
	 * @param queryString the query string used for the search
	 * @param index the inverted index used for the search
	 * @param path the path of the file being searched
	 * @param value the set of matching positions within the file
	 */
	public static void calculateResult(TreeMap<String, ArrayList<InvertedIndex.IndexSearcher>> result, String queryString,
			InvertedIndex index, String path, Set<Integer> value) {
		ArrayList<IndexSearcher> searchers = result.computeIfAbsent(queryString, k -> new ArrayList<>());
		int totalMatches = value.size();

		IndexSearcher existingSearcher = findSearcherForPath(searchers, path);
		if (existingSearcher != null) {
			existingSearcher.addCount(totalMatches);
			existingSearcher.setScore(calculateScore(index, path, existingSearcher.getCount()));
		}
		else {
			Double score = calculateScore(index, path, totalMatches);
			IndexSearcher newSearcher = index.new IndexSearcher(totalMatches, score, path);
			searchers.add(newSearcher);
		}
	}

	/**
	 * Finds an existing IndexSearcher for the given path from the provided list of
	 * searchers.
	 *
	 * @param searchers the list of searchers to search within
	 * @param path the path to match against existing searchers
	 * @return the existing IndexSearcher if found, otherwise null
	 */
	private static IndexSearcher findSearcherForPath(ArrayList<IndexSearcher> searchers, String path) {
		for (IndexSearcher searcher : searchers) {
			if (filePathMatch(searcher, path)) {
				return searcher;
			}
		}
		return null;
	}

	/**
	 * Performs partial search based on the provided query, updating the result map
	 * with search results.
	 * 
	 * @param query The query terms to search for.
	 * @param index The inverted index to search within.
	 * @param result The map to store the search results, where each query term maps
	 *   to a list of IndexSearchers.
	 */
	public static void partialSearch(TreeSet<String> query, InvertedIndex index,
			TreeMap<String, ArrayList<InvertedIndex.IndexSearcher>> result) {
		String queryString = treeSetToString(query);

		for (String queryTerm : query) {
			ArrayList<InvertedIndex.IndexSearcher> termResults = new ArrayList<>();

			for (String word : index.viewWords()) {
				if (word.startsWith(queryTerm)) {
					for (String location : index.viewLocations(word)) {
						Set<Integer> positions = index.viewPositions(word, location);
						updateSearchResults(termResults, location, positions, index);
					}
				}
			}

			mergeResults(result.computeIfAbsent(queryString, k -> new ArrayList<>()), termResults, index);
		}
	}

	/**
	 * Updates the search results for a specific location with the provided set of
	 * positions. If an IndexSearcher for the location already exists in the list of
	 * searchers, it updates its count and score. Otherwise, a new IndexSearcher is
	 * created for the location.
	 *
	 * @param searchers the list of searchers to update or add to
	 * @param location the location of the matched word positions
	 * @param positions the set of positions where the word is found in the location
	 * @param index the inverted index used for score calculation
	 */
	private static void updateSearchResults(ArrayList<InvertedIndex.IndexSearcher> searchers, String location,
			Set<Integer> positions, InvertedIndex index) {
		IndexSearcher searcher = findOrCreateSearcher(index, searchers, location);
		searcher.addCount(positions.size());
		searcher.setScore(calculateScore(index, location, searcher.getCount()));
	}

	/**
	 * Finds an existing IndexSearcher for the given location from the provided list
	 * of searchers, or creates a new one if not found.
	 * 
	 * @param index Inverted Index instances
	 * @param searchers the list of searchers to search within
	 * @param location the location to match against existing searchers
	 * @return the existing or newly created IndexSearcher
	 */
	private static IndexSearcher findOrCreateSearcher(InvertedIndex index,
			ArrayList<InvertedIndex.IndexSearcher> searchers, String location) {
		for (IndexSearcher searcher : searchers) {
			if (searcher.getWhere().equals(location)) {
				return searcher;
			}
		}

		IndexSearcher newSearcher = index.new IndexSearcher(0, 0.0, location);
		searchers.add(newSearcher);
		return newSearcher;
	}

	/**
	 * Merges the results of query term searches into main results and sorts them.
	 *
	 * @param mainResults The main list of search results.
	 * @param termResults The list of search results from a term search.
	 * @param index The InvertedIndex containing the indexed data.
	 */
	private static void mergeResults(ArrayList<IndexSearcher> mainResults, ArrayList<IndexSearcher> termResults,
			InvertedIndex index) {
		for (IndexSearcher termSearcher : termResults) {
			boolean found = false;
			for (IndexSearcher mainSearcher : mainResults) {
				if (mainSearcher.getWhere().equals(termSearcher.getWhere())) {
					mainSearcher.addCount(termSearcher.getCount());
					mainSearcher.setScore(calculateScore(index, termSearcher.getWhere().toString(), mainSearcher.getCount()));
					found = true;
					break;
				}
			}
			if (!found) {
				mainResults.add(termSearcher);
			}
		}
		Collections.sort(mainResults);
	}

	/**
	 * Checks if the file path in the given IndexSearcher matches the specified
	 * path.
	 *
	 * @param searcher The IndexSearcher object containing the file path to compare.
	 * @param path The file path to compare against.
	 * @return {@code true} if the file path in the IndexSearcher matches the
	 *   specified path, {@code false} otherwise.
	 */
	private static boolean filePathMatch(IndexSearcher searcher, String path) {
		return (searcher.getWhere().toString().equalsIgnoreCase(path));
	}

	/**
	 * Calculates the score for a given search result based on the total number of
	 * matches and total words in the document.
	 *
	 * @param index The inverted index containing word count information.
	 * @param path The path of the document to calculate the score for.
	 * @param totalMatches The total number of matches for the query term in the
	 *   document.
	 * @return The calculated score as a formatted string.
	 */
	private static Double calculateScore(InvertedIndex index, String path, int totalMatches) {

		int totalWords = findTotalWords(index, path);
		return (double) totalMatches / totalWords;

	}

	/**
	 * Finds the total words in a path
	 * 
	 * @param index The inverted index containing word count information.
	 * @param path The path of the document to count the words for.
	 * @return The total words in path.
	 */
	private static int findTotalWords(InvertedIndex index, String path) {
		return index.getWordCount(path);
	}

	/**
	 * Converts the elements of a TreeSet into a single string using
	 * {@link StringBuilder}.
	 *
	 * @param treeSet The TreeSet to convert into a string.
	 * @return A string representation of the TreeSet elements.
	 */
	private static String treeSetToString(TreeSet<String> treeSet) {
		return String.join(" ", treeSet);
	}

	/**
	 * Represents a search result in the inverted index, including the count of
	 * matches, score, and document path.
	 */
	public class IndexSearcher implements Comparable<IndexSearcher> {

		/** The count of matches. */
		public int count;

		/** The score of the search result. */
		public Double score;

		/** The path of the document containing the matches. */
		public String where;

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
		}

		/**
		 * Sets the count of matches to the specified value.
		 *
		 * @param count The value to set as the count of matches.
		 */
		public void setCount(int count) {
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
		public void setScore(Double score) {
			this.score = score;
		}

		/**
		 * Sets the path of the document containing the matches.
		 *
		 * @param where The path to set.
		 */
		public void setWhere(String where) {
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
			DecimalFormat FORMATTER = new DecimalFormat("0.00000000");

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
			builder.append("  \"score\": ").append(formatScore(score)).append(",\n"); // If score is a number, no need for
			// quotes
			builder.append("  \"where\": \"").append(where).append("\"\n");
			builder.append("}");
			return builder.toString();
		}

	}

}
