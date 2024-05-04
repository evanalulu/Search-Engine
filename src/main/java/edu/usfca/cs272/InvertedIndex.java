package edu.usfca.cs272;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
	 * Adds the position of a word in a document to the index map.
	 *
	 * @param word the word to add
	 * @param location the path of the document
	 * @param position the position of the word in the document
	 */
	public void addWord(String word, String location, Integer position) {
		indexMap.computeIfAbsent(word, k -> new TreeMap<>()).computeIfAbsent(location, k -> new TreeSet<>()).add(position);

		wordCountMap.merge(location, position, Integer::max);
	}

	/**
	 * Adds all entries from the specified inverted index to this inverted index.
	 *
	 * @param other the inverted index containing entries to be added to this
	 *   inverted index
	 */
	public void addAll(InvertedIndex other) {
		for (var wordEntry : other.indexMap.entrySet()) {
			TreeMap<String, TreeSet<Integer>> wordMap = this.indexMap.get(wordEntry.getKey());

			if (wordMap == null) {
				this.indexMap.put(wordEntry.getKey(), wordEntry.getValue());
			}
			else {
				for (var locationEntry : wordEntry.getValue().entrySet()) {
					TreeSet<Integer> locationMap = wordMap.get(locationEntry.getKey());

					if (locationMap == null) {
						wordMap.put(locationEntry.getKey(), locationEntry.getValue());
					}
					else {
						locationMap.addAll(locationEntry.getValue());
					}
				}
			}
		}

		for (var otherEntry : other.wordCountMap.entrySet()) {
			this.wordCountMap.merge(otherEntry.getKey(), otherEntry.getValue(), Integer::max);
		}
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
	 * Returns the number of documents indexed.
	 *
	 * @return the number of indexed documents
	 */
	public int getFileCount() {
		return wordCountMap.size();
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

	/**
	 * Performs either exact o partial search for the specified set of query terms
	 * in the inverted index depending on the value of the isPartial.
	 *
	 * @param query the set of query terms to be searched for in the inverted index
	 * @param isPartial a boolean indicating whether to perform a partial search
	 *   (true) or an exact search (false)
	 * @return an ArrayList containing IndexSearcher objects representing the search
	 *   results, sorted based on the calculated scores in descending order
	 */
	public ArrayList<IndexSearcher> search(TreeSet<String> query, boolean isPartial) {
		return (isPartial) ? partialSearch(query) : exactSearch(query);
	}

	/**
	 * Performs an exact search for the specified set of query terms in the inverted
	 * index.
	 *
	 * @param query the set of query terms to be searched for in the inverted index
	 * @return an ArrayList containing IndexSearcher objects representing the exact
	 *   search results, sorted based on the calculated scores in descending order
	 */
	public ArrayList<IndexSearcher> exactSearch(TreeSet<String> query) {
		ArrayList<IndexSearcher> results = new ArrayList<>();
		Map<String, IndexSearcher> lookup = new HashMap<>();

		for (String queryTerm : query) {
			TreeMap<String, TreeSet<Integer>> locations = indexMap.get(queryTerm);
			if (locations != null) {
				for (var entry : locations.entrySet()) {
					processSearchResult(entry.getKey(), entry.getValue().size(), lookup, results);
				}
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * Performs a partial search for the specified set of queries in the inverted
	 * index.
	 * 
	 * @param queries the set of query strings to be partially searched in the
	 *   inverted index
	 * @return an ArrayList containing IndexSearcher objects representing the
	 *   partial search results, sorted based on the calculated scores in descending
	 *   order
	 */
	public ArrayList<IndexSearcher> partialSearch(Set<String> queries) {
		ArrayList<IndexSearcher> results = new ArrayList<>();
		Map<String, IndexSearcher> lookup = new HashMap<>();

		for (String query : queries) {
			for (var outerEntry : indexMap.tailMap(query).entrySet()) {
				if (outerEntry.getKey().startsWith(query)) {
					for (var innerEntry : outerEntry.getValue().entrySet()) {
						processSearchResult(innerEntry.getKey(), innerEntry.getValue().size(), lookup, results);
					}
				}
				else {
					break;
				}
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * Processes a search result by updating an existing IndexSearcher or creating a
	 * new one. If an IndexSearcher already exists for the location, its score is
	 * updated based on the matches. Otherwise, a new IndexSearcher is created, its
	 * score is calculated, and it's added to the results list and lookup map.
	 *
	 * @param location the location associated with the search result
	 * @param matches the number of matches found at the location
	 * @param lookup the map used to look up existing IndexSearchers by location
	 * @param results the list containing the search results
	 */
	private void processSearchResult(String location, int matches, Map<String, IndexSearcher> lookup,
			ArrayList<IndexSearcher> results) {
		IndexSearcher current = lookup.get(location);
		if (current != null) {
			current.calculateScore(matches);
		}
		else {
			IndexSearcher newSearcher = new IndexSearcher(location);
			newSearcher.calculateScore(matches);
			results.add(newSearcher);
			lookup.put(location, newSearcher);
		}
	}

	/**
	 * Represents a search result in the inverted index, including the count of
	 * matches, score, and document path.
	 */
	public class IndexSearcher implements Comparable<IndexSearcher> {

		/** The count of matches. */
		private int count;

		/** The score of the search result. */
		private double score;

		/** The path of the document containing the matches. */
		private final String where;

		/**
		 * The decimal format used for formatting numbers with eight decimal places.
		 */
		private static DecimalFormat FORMATTER = new DecimalFormat("0.00000000");

		/**
		 * Constructs an IndexSearcher object with the given parameters.
		 *
		 * @param where The path of the document containing the matches.
		 */
		public IndexSearcher(String where) {
			this.count = 0;
			this.score = 0.0;
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
		private void calculateScore(int count) {
			this.count += count;
			this.score = (double) this.count / wordCountMap.get(this.where);
		}

		/**
		 * Retrieves the score of the search result.
		 *
		 * @return The score of the search result.
		 */
		public double getScore() {
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

		/**
		 * Writes the IndexSearcher object to JSON format and outputs it to the
		 * specified writer with the given indentation level.
		 *
		 * @param writer the writer to which the JSON representation is written
		 * @param level the indentation level to be used in the JSON output
		 * @throws IOException if an I/O error occurs while writing to the writer
		 */
		public void toJson(Writer writer, int level) throws IOException {
			String indent = "  ".repeat(level);
			writer.write(indent + "{\n");
			writer.write(indent + "  \"count\": " + count + ",\n");
			writer.write(indent + "  \"score\": " + formatScore(score) + ",\n");
			writer.write(indent + "  \"where\": \"" + where.replace("\\", "\\\\") + "\"\n");
			writer.write(indent + "}");
		}
	}

}
