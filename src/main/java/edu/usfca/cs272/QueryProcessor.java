package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

	/*
	 * TODO
	 * 
	 * Think about what makes sense to make a final member versus a parameter that
	 * can change each call in a method...
	 * 
	 * processQueries(hello.txt, true) "hello world" --> world.txt, earth.txt
	 * 
	 * processQueries(hello.txt, false) "hello world" --> earth.txt
	 * 
	 * Make the search mode (partial or exact) something set the same way as the
	 * index so it can't change every method call
	 */

	/**
	 * Reads queries from a file and performs search on an inverted index.
	 * 
	 * @param path The path to the file containing queries.
	 * @param isPartial If -partial search is requested
	 * @throws IOException If an I/O error occurs while reading the query file.
	 */
	public void processQueries(Path path, Boolean isPartial) throws IOException {
		/*
		 * TODO try (BufferedReader reader = Files.newBufferedReader(path)) { String
		 * line; while ((line = reader.readLine()) != null) { processQueries(line, ...)
		 * }
		 */

		Set<TreeSet<String>> queries = getQuery(path);
		for (TreeSet<String> querySet : queries) {
			ArrayList<InvertedIndex.IndexSearcher> results = index.search(querySet, isPartial);
			searchResult.put(String.join(" ", querySet), results);
		}
	}

	/*
	 * TODO public void processQueries(String line, ...) { stem (ideally reusing a
	 * SnowBallStemmer) join decide if need to search storing the search results }
	 */

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
	 * Retrieves the number of queries processed and stored in the search result
	 * map.
	 *
	 * @return the number of queries processed
	 */
	public int numQueryLines() {
		return searchResult.size();
	}

	/**
	 * Retrieves the number of search results associated with the specified query.
	 *
	 * @param query the query for which to retrieve the number of search results
	 * @return the number of search results for the query
	 */
	public int numResults(String query) {
		String joinedQuery = String.join(" ", FileStemmer.uniqueStems(query));
		List<IndexSearcher> results = searchResult.get(joinedQuery);

		return (results != null) ? results.size() : 0;
	}

	/**
	 * Checks if the search result map contains search results for the specified
	 * query line.
	 *
	 * @param line the query line to be checked
	 * @return true if search results exist for the query line, false otherwise
	 */
	public boolean hasQueryLine(String line) {
		String queryString = String.join(" ", FileStemmer.uniqueStems(line));
		return searchResult.containsKey(queryString);
	}

	/**
	 * Checks if search results exist for the specified query.
	 *
	 * @param query the query to be checked for search results
	 * @return true if search results exist for the query, false otherwise
	 */
	public boolean hasResult(String query) {
		String joinedQuery = String.join(" ", FileStemmer.uniqueStems(query));
		return searchResult.containsKey(joinedQuery) && !searchResult.get(joinedQuery).isEmpty();
	}

	/**
	 * Retrieves an unmodifiable set of query strings stored in the search result
	 * map.
	 *
	 * @return an unmodifiable set containing the query strings for which search
	 *   results are stored
	 */
	public Set<String> viewQueries() {
		return Collections.unmodifiableSet(searchResult.keySet());
	}

	/**
	 * Retrieves an unmodifiable list of search results associated with the
	 * specified query line.
	 *
	 * @param query the query line for which to retrieve search results
	 * @return an unmodifiable list containing IndexSearcher objects representing
	 *   the search results for the query line, or an empty list if no search
	 *   results exist for the query line
	 */
	public List<IndexSearcher> viewResults(String query) {
		String queryString = String.join(" ", FileStemmer.uniqueStems(query));
		ArrayList<IndexSearcher> searchers = searchResult.get(queryString);

		return (searchers != null) ? Collections.unmodifiableList(new ArrayList<>(searchers)) : Collections.emptyList();

	}
}
