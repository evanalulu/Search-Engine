package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A class responsible for processing queries using a work queue and managing
 * search results.
 */
public class QueuedQueryProcessor {

	/**
	 * The map storing search results, where keys represent query strings and values
	 * represent lists of searchers.
	 */
	private final TreeMap<String, ArrayList<ThreadSafeInvertedIndex.IndexSearcher>> searchResult;

	/**
	 * The thread-safe inverted index used for query processing and search result
	 * management.
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * The work queue for executing query processing tasks.
	 */
	private final WorkQueue queue;

	/**
	 * Constructs a QueuedQueryProcessor with the specified thread-safe inverted
	 * index, partial search flag, and work queue.
	 *
	 * @param index the thread-safe inverted index to be used for query processing
	 *   and search result management
	 * @param usePartial a boolean indicating whether to use partial search (true)
	 *   or exact search (false)
	 * @param queue the work queue for executing query processing tasks
	 */
	public QueuedQueryProcessor(ThreadSafeInvertedIndex index, boolean usePartial, WorkQueue queue) {
		this.index = index;
		this.queue = queue;
		this.searchResult = new TreeMap<>();
	}

	/**
	 * Processes queries stored in a file specified by the input path.
	 *
	 * @param path the path to the file containing query sets to be processed
	 * @param isPartial a boolean indicating whether to use partial search (true) or
	 *   exact search (false)
	 * @throws IOException if an I/O error occurs while reading the query file or
	 *   processing queries
	 */
	public void processQueries(Path path, Boolean isPartial) throws IOException {
		Set<TreeSet<String>> queries = getQuery(path);
		for (TreeSet<String> querySet : queries) {
			queue.execute(new Task(querySet, isPartial));
		}
		queue.finish();
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
		Set<TreeSet<String>> queries = new HashSet<>();

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}

				String[] words = FileStemmer.parse(line);
				String wordsString = String.join(" ", words);
				if (!wordsString.isEmpty()) {
					queries.add(FileStemmer.uniqueStems(wordsString));
				}
			}
		}
		return queries;
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
	 * A task representing the processing of a set of query terms. When executed, it
	 * performs either a partial or exact search based on the provided flag.
	 */
	private class Task implements Runnable {

		/**
		 * The set of query terms to be processed.
		 */
		private final TreeSet<String> queries;

		/**
		 * A boolean indicating whether to perform a partial search (true) or an exact
		 * search (false).
		 */
		private final boolean isPartial;

		/**
		 * Constructs a Task with the specified set of query terms and partial search
		 * flag.
		 *
		 * @param querySet the set of query terms to be processed
		 * @param isPartial a boolean indicating whether to perform a partial search
		 *   (true) or an exact search (false)
		 */
		public Task(TreeSet<String> querySet, boolean isPartial) {
			this.queries = querySet;
			this.isPartial = isPartial;
		}

		/**
		 * Executes the task to perform a search for the set of query terms.
		 */
		@Override
		public void run() {
			String queryString = String.join(" ", queries);

			ArrayList<ThreadSafeInvertedIndex.IndexSearcher> results = index.search(queries, isPartial);

			synchronized (searchResult) {
				searchResult.put(queryString, results);
			}

		}
	}
}
