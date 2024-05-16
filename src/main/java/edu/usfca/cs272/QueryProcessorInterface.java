package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import edu.usfca.cs272.InvertedIndex.IndexSearcher;

/**
 * The QueryProcessorInterface defines methods for processing queries from a
 * file and performing searches on an inverted index.
 * 
 * This interface provides methods for reading queries from a file, processing
 * each query line, retrieving statistics about the processed queries, and
 * checking for the existence of search results associated with specific query
 * lines.
 */
public interface QueryProcessorInterface {

	/**
	 * Reads queries from a file and performs search on an inverted index.
	 * 
	 * @param path The path to the file containing queries.
	 * @throws IOException If an I/O error occurs while reading the query file.
	 */
	public default void processQueries(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				processQueries(line);
			}
		}
	}

	/**
	 * Processes a single query line.
	 * 
	 * @param line The query line to process.
	 */
	public abstract void processQueries(String line);

	/**
	 * Retrieves the number of query lines processed by the QueuedQueryProcessor.
	 *
	 * @return the number of query lines processed
	 */
	public abstract int numQueryLines();

	/**
	 * Retrieves the number of search results associated with the specified query.
	 *
	 * @param query the query for which to retrieve the number of search results
	 * @return the number of search results for the query
	 */
	public abstract int numResults(String query);

	/**
	 * Checks if the search result map contains search results for the specified
	 * query line.
	 *
	 * @param queryLine the query line to be checked
	 * @return true if search results exist for the query line, false otherwise
	 */
	public abstract boolean hasQueryLine(String queryLine);

	/**
	 * Retrieves an unmodifiable set of query strings stored in the search result
	 * map.
	 *
	 * @return an unmodifiable set containing the query strings for which search
	 *   results are stored
	 */
	public abstract Set<String> viewQueries();

	/**
	 * Retrieves an unmodifiable list of search results associated with the
	 * specified query line.
	 *
	 * @param query the query line for which to retrieve search results
	 * @return an unmodifiable list containing IndexSearcher objects representing
	 *   the search results for the query line, or an empty list if no search
	 *   results exist for the query line
	 */
	public abstract List<IndexSearcher> viewResults(String query);

	/**
	 * Constructs a query string by stemming the input query and joining the stemmed
	 * words with spaces.
	 *
	 * @param query the query to be stemmed and joined
	 * @return the constructed query string
	 */
	public abstract String getQueryString(String query);

	/**
	 * Writes the search result map to a JSON file specified by the given output
	 * path.
	 *
	 * @param output the path to the output JSON file
	 * @throws IOException if an I/O error occurs while writing the JSON file
	 */
	public abstract void writeSearchResults(Path output) throws IOException;
}
