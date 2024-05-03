package edu.usfca.cs272;

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
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * A class responsible for processing queries using a work queue and managing
 * search results.
 */
public class QueuedQueryProcessor {

	/**
	 * The thread-safe inverted index used for query processing and search result
	 * management.
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * A boolean flag indicating whether the search mode is set to partial (true) or
	 * exact (false).
	 */
	private final boolean isPartial;

	/**
	 * The work queue used for processing tasks asynchronously.
	 */
	private final WorkQueue queue;

	/**
	 * The map storing search results, where keys represent query strings and values
	 * represent lists of searchers.
	 */
	private final TreeMap<String, ArrayList<ThreadSafeInvertedIndex.IndexSearcher>> searchResult;

	/**
	 * Constructs a QueuedQueryProcessor with the specified thread-safe inverted
	 * index, partial search flag, and work queue.
	 *
	 * @param index the thread-safe inverted index to be used for query processing
	 *   and search result management
	 * @param isPartial a boolean indicating whether to use partial search (true) or
	 *   exact search (false)
	 * @param queue the work queue for processing tasks asynchronously
	 * 
	 */
	public QueuedQueryProcessor(ThreadSafeInvertedIndex index, boolean isPartial, WorkQueue queue) {
		this.index = index;
		this.isPartial = isPartial;
		this.queue = queue;
		this.searchResult = new TreeMap<>();
	}

	/**
	 * Processes queries stored in a file specified by the input path.
	 *
	 * @param path the path to the file containing query sets to be processed exact
	 *   search (false)
	 * @throws IOException if an I/O error occurs while reading the query file or
	 *   processing queries
	 */
	public void processQueries(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				queue.execute(new Task(line, isPartial));
			}
			queue.join();
		}
	}

	/**
	 * Retrieves the number of query lines processed by the QueuedQueryProcessor.
	 *
	 * @return the number of query lines processed
	 */
	public int numQueryLines() {
		synchronized (searchResult) {
			return searchResult.size();

		}
	}

	/**
	 * Retrieves the number of search results associated with the specified query.
	 *
	 * @param query the query for which to retrieve the number of search results
	 * @return the number of search results for the query
	 */
	public int numResults(String query) {
		synchronized (searchResult) {
			List<IndexSearcher> results = searchResult.get(getQuerySting(query));
			return (results != null) ? results.size() : 0;
		}
	}

	/**
	 * Checks if the search result map contains search results for the specified
	 * query line.
	 *
	 * @param queryLine the query line to be checked
	 * @return true if search results exist for the query line, false otherwise
	 */
	public boolean hasQueryLine(String queryLine) {
		synchronized (searchResult) {
			return searchResult.containsKey(getQuerySting(queryLine));
		}
	}

	/**
	 * Checks if search results exist for the specified query.
	 *
	 * @param query the query to be checked for search results
	 * @return true if search results exist for the query, false otherwise
	 */
	public boolean hasResult(String query) {
		String queryString = getQuerySting(query);
		synchronized (searchResult) {
			return searchResult.containsKey(queryString) && !searchResult.get(queryString).isEmpty();
		}
	}

	/**
	 * Retrieves an unmodifiable set of query strings stored in the search result
	 * map.
	 *
	 * @return an unmodifiable set containing the query strings for which search
	 *   results are stored
	 */
	public Set<String> viewQueries() {
		synchronized (searchResult) {
			return Collections.unmodifiableSet(searchResult.keySet());
		}
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
		synchronized (searchResult) {
			ArrayList<IndexSearcher> searchers = searchResult.get(getQuerySting(query));
			return (searchers != null) ? Collections.unmodifiableList(new ArrayList<>(searchers)) : Collections.emptyList();
		}
	}

	/**
	 * Constructs a query string by stemming the input query and joining the stemmed
	 * words with spaces.
	 *
	 * @param query the query to be stemmed and joined
	 * @return the constructed query string
	 */
	private static String getQuerySting(String query) {
		return String.join(" ", FileStemmer.uniqueStems(query));
	}

	/**
	 * Writes the search result map to a JSON file specified by the given output
	 * path.
	 *
	 * @param output the path to the output JSON file
	 * @throws IOException if an I/O error occurs while writing the JSON file
	 */
	public void writeSearchResults(Path output) throws IOException {
		synchronized (searchResult) {
			JsonWriter.writeSearchResults(searchResult, output);
		}
	}

	/**
	 * A task representing the processing of a set of query terms. When executed, it
	 * performs either a partial or exact search based on the provided flag.
	 */
	private class Task implements Runnable {

		/**
		 * The set of query terms to be processed.
		 */
		private final String line;

		/**
		 * A boolean indicating whether to perform a partial search (true) or an exact
		 * search (false).
		 */
		private final boolean isPartial;

		/**
		 * The stemmer used for stemming words.
		 */
		private final Stemmer localStemmer;

		/**
		 * Constructs a Task with the specified set of query terms and partial search
		 * flag.
		 *
		 * @param line the set of query terms to be processed
		 * @param isPartial a boolean indicating whether to perform a partial search
		 *   (true) or an exact search (false)
		 */
		public Task(String line, boolean isPartial) {
			this.line = line;
			this.isPartial = isPartial;
			this.localStemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			;
		}

		/**
		 * Executes the task to perform a search for the set of query terms.
		 */
		@Override
		public void run() {
			TreeSet<String> query = new TreeSet<>();

			String[] words = FileStemmer.parse(line);

			for (String word : words) {
				query.add(localStemmer.stem(word).toString());
			}
			String queryString = String.join(" ", query);

			synchronized (searchResult) {
				if (queryString.isEmpty() || searchResult.containsKey(queryString)) {
					return;
				}
				searchResult.put(queryString, null);
			}

			ArrayList<ThreadSafeInvertedIndex.IndexSearcher> results = index.search(query, isPartial);

			synchronized (searchResult) {
				searchResult.put(queryString, results);
			}

		}
	}
}
