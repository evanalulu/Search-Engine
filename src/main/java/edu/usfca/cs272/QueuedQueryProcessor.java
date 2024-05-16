package edu.usfca.cs272;

import java.io.IOException;
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
public class QueuedQueryProcessor implements QueryProcessorInterface {

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
	 * @param line the path to the file containing query sets to be processed exact
	 *   search (false)
	 */
	@Override
	public void processQueries(String line) {
		queue.execute(new Task(line, isPartial));
		queue.finish();
	}

	@Override
	public int numQueryLines() {
		synchronized (searchResult) {
			return viewQueries().size();
		}
	}

	@Override
	public int numResults(String query) {
		synchronized (searchResult) {
			return viewResults(query).size();
		}
	}

	@Override
	public boolean hasQueryLine(String queryLine) {
		String cleanedLine = getQueryString(queryLine);
		synchronized (searchResult) {
			return viewQueries().contains(cleanedLine);
		}
	}

	@Override
	public Set<String> viewQueries() {
		synchronized (searchResult) {
			return Collections.unmodifiableSet(searchResult.keySet());
		}
	}

	@Override
	public List<IndexSearcher> viewResults(String query) {
		String queryString = getQueryString(query);
		synchronized (searchResult) {
			ArrayList<IndexSearcher> searchers = searchResult.get(queryString);
			return (searchers != null) ? Collections.unmodifiableList(searchers) : Collections.emptyList();
		}
	}

	@Override
	public String getQueryString(String query) {
		return String.join(" ", FileStemmer.uniqueStems(query));
	}

	@Override
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
		 * The local stemmer used for stemming words.
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

		@Override
		public void run() {

			TreeSet<String> query = FileStemmer.uniqueStems(line, localStemmer);
			String queryString = String.join(" ", query);

			synchronized (searchResult) {
				if (queryString.isEmpty() || searchResult.containsKey(queryString)) {
					return;
				}
			}

			ArrayList<ThreadSafeInvertedIndex.IndexSearcher> results = index.search(query, isPartial);

			synchronized (searchResult) {
				searchResult.put(queryString, results);
			}

		}
	}
}
