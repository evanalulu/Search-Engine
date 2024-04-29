package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.usfca.cs272.InvertedIndex.IndexSearcher;

/**
 * A class responsible for processing queries and managing search results.
 */
public class QueryProcessor {

	/**
	 * The inverted index used for query processing and search result management.
	 */
	private final InvertedIndex index;

	/**
	 * The map storing search results, where keys represent query strings and values
	 * represent lists of searchers.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.IndexSearcher>> searchResult;

	/**
	 * Constructs a QueryProcessor with the specified inverted index. Initializes
	 * the search result map as an empty TreeMap.
	 *
	 * @param index the inverted index to be used for query processing
	 */
	public QueryProcessor(InvertedIndex index) {
		this.index = index;
		this.searchResult = new TreeMap<>();
	}

	/**
	 * Reads queries from a file and performs search on an inverted index.
	 * 
	 * @param path The path to the file containing queries.
	 * @param isPartial If -partial search is requested
	 * @throws IOException If an I/O error occurs while reading the query file.
	 */
	public void processQueries(Path path, Boolean isPartial) throws IOException {
		Set<TreeSet<String>> queries = getQuery(path);
		for (TreeSet<String> querySet : queries) {
			ArrayList<InvertedIndex.IndexSearcher> results = index.search(querySet, isPartial);
			searchResult.put(String.join(" ", querySet), results);
		}
	}

	/**
	 * Retrieves query terms from a file and returns a set of unique stemmed query
	 * terms.
	 *
	 * @param path The path to the file containing queries.
	 * @return A set of unique stemmed query terms, where each query is represented
	 *   as a sorted set of terms.
	 * @throws IOException If an I/O error occurs while reading the query file.
	 */
	private static Set<TreeSet<String>> getQuery(Path path) throws IOException {
		Set<TreeSet<String>> query = new HashSet<>();

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}

				String[] words = FileStemmer.parse(line);
				String wordsString = String.join(" ", words);
				if (!wordsString.isEmpty()) {
					query.add(FileStemmer.uniqueStems(wordsString));
				}
			}
		}
		return query;
	}

	/**
	 * Writes the search result map to a JSON file specified by the given output
	 * path.
	 *
	 * @param output the path to the output JSON file
	 * @throws IOException if an I/O error occurs while writing the JSON file
	 */
	public void writeSearchResults(Path output) throws IOException {
		JsonWriter.writeSearchResults(searchResult, output);
	}

	/**
	 * Returns the number of results in result map.
	 *
	 * @return the number of results in result map
	 */
	public int getResultCount() {
		return searchResult.size();
	}

	/**
	 * Checks if there are any search results.
	 *
	 * @return {@code true} if there are any results, {@code false} otherwise.
	 */
	public boolean hasResults() {
		return !searchResult.isEmpty();
	}

	/**
	 * Checks if a specific word is indexed in search results.
	 *
	 * @param word the word to check
	 * @return {@code true} if the word is indexed
	 */
	public boolean hasWord(String word) {
		return searchResult.containsKey(word);
	}

	/**
	 * Retrieves an unmodifiable set of words present in the search result map.
	 *
	 * @return an unmodifiable set containing the unique words for which search
	 *   results are available
	 */
	public Set<String> viewWords() {
		return Collections.unmodifiableSet(searchResult.keySet());
	}

	/**
	 * Retrieves an unmodifiable collection of IndexSearcher objects associated with
	 * the specified word. If search results exist for the word, the method returns
	 * an unmodifiable collection containing those searchers. If no search results
	 * exist for the word, an empty collection is returned.
	 *
	 * @param word the word for which to retrieve searchers
	 * @return an unmodifiable collection of IndexSearcher objects associated with
	 *   the specified word, or an empty collection if no search results exist for
	 *   the word
	 */
	public Collection<IndexSearcher> viewSearchers(String word) {
		ArrayList<IndexSearcher> searchers = searchResult.get(word);
		if (searchers != null) {
			return Collections.unmodifiableCollection(searchers);
		}
		return Collections.emptyList();
	}
}
