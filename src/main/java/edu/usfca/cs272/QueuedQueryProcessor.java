package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

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
	 * The stemmer used for stemming words.
	 */
	private final Stemmer stemmer;

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
		this.stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
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
	public void processQueries(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					queue.execute(new Task(line, isPartial));
				}
			}
		}
		queue.finish();
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
		private final String queryLine;

		/**
		 * A boolean indicating whether to perform a partial search (true) or an exact
		 * search (false).
		 */
		private final boolean isPartial;

		/**
		 * Constructs a Task with the specified set of query terms and partial search
		 * flag.
		 *
		 * @param queryLine the set of query terms to be processed
		 * @param isPartial a boolean indicating whether to perform a partial search
		 *   (true) or an exact search (false)
		 */
		public Task(String queryLine, boolean isPartial) {
			this.queryLine = queryLine;
			this.isPartial = isPartial;
		}

		/**
		 * Executes the task to perform a search for the set of query terms.
		 */
		@Override
		public void run() {
			TreeSet<String> querySet = FileStemmer.uniqueStems(queryLine);
			String queryString = String.join(" ", querySet);

			synchronized (searchResult) {
				if (queryString.isEmpty() || searchResult.containsKey(queryString)) {
					return;
				}
				searchResult.put(queryString, null);
			}

			ArrayList<ThreadSafeInvertedIndex.IndexSearcher> results = index.search(querySet, isPartial);

			synchronized (searchResult) {
				searchResult.put(queryString, results);
			}

		}
	}
}
