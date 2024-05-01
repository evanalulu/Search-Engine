package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.usfca.cs272.InvertedIndex.IndexSearcher;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A class responsible for processing queries and managing search results.
 */
public class QueryProcessor {

	/**
	 * The inverted index used for query processing and search result management.
	 */
	private final InvertedIndex index;

	/**
	 * A boolean flag indicating whether the search mode is set to partial (true) or
	 * exact (false).
	 */
	private final boolean isPartial;

	/**
	 * The stemmer used for stemming words.
	 */
	private final Stemmer stemmer;

	/**
	 * The map storing search results, where keys represent query strings and values
	 * represent lists of searchers.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.IndexSearcher>> searchResult;

	/**
	 * Constructs a QueryProcessor with the specified inverted index, search mode,
	 * and stemmer.
	 *
	 * @param index the inverted index to be used for query processing
	 * @param isPartial a boolean indicating whether to use partial search (true) or
	 *   exact search (false)
	 */
	public QueryProcessor(InvertedIndex index, boolean isPartial) {
		this.index = index;
		this.isPartial = isPartial;
		this.stemmer = new SnowballStemmer(ENGLISH);
		this.searchResult = new TreeMap<>();
	}

	/**
	 * Reads queries from a file and performs search on an inverted index.
	 * 
	 * @param path The path to the file containing queries.
	 * @throws IOException If an I/O error occurs while reading the query file.
	 */
	public void processQueries(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				processQueryLine(line);
			}
		}
	}

	/**
	 * Processes a single query line by stemming the words, constructing a query
	 * string, and performing a search in the inverted index. If the query string is
	 * empty or search results already exist for the query, the method returns
	 * without further processing.
	 *
	 * @param line the query line to be processed
	 */
	private void processQueryLine(String line) {
		TreeSet<String> query = new TreeSet<>();

		String[] words = FileStemmer.parse(line);

		for (String word : words) {
			query.add(stemmer.stem(word).toString());
		}
		String queryString = String.join(" ", query);

		if (queryString.isEmpty() || searchResult.containsKey(queryString)) {
			return;
		}

		ArrayList<InvertedIndex.IndexSearcher> results = index.search(query, isPartial);
		searchResult.put(queryString, results);
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

	// TODO: maybe extra function to filestemmer.uniquestems
}
