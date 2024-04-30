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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueuedQueryProcessor {

	private final TreeMap<String, ArrayList<ThreadSafeInvertedIndex.IndexSearcher>> searchResult;
	private final ThreadSafeInvertedIndex index;
	private final WorkQueue queue;

	public QueuedQueryProcessor(ThreadSafeInvertedIndex index, boolean usePartial, WorkQueue queue) {
		this.index = index;
		this.queue = queue;
		this.searchResult = new TreeMap<>();
	}

	public void processQueries(Path path, Boolean isPartial) throws IOException {
		Set<TreeSet<String>> queries = getQuery(path);
		for (TreeSet<String> querySet : queries) {
			queue.execute(new Task(querySet, isPartial));
		}
		queue.finish();
		System.out.println("Finished processing all queries.");

	}

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

	private class Task implements Runnable {
		private final TreeSet<String> querySet;
		private final boolean isPartial;

		public Task(TreeSet<String> querySet, boolean isPartial) {
			this.querySet = querySet;
			this.isPartial = isPartial;
		}

		@Override
		public void run() {
			Logger log = LogManager.getLogger();
			String queryString = String.join(" ", querySet);
			log.info("Processing query");

			ArrayList<ThreadSafeInvertedIndex.IndexSearcher> results = index.search(querySet, isPartial);

			synchronized (searchResult) {
				searchResult.put(queryString, results);
			}

		}
	}

	public TreeMap<String, ArrayList<ThreadSafeInvertedIndex.IndexSearcher>> getResults() {
		synchronized (searchResult) {
			return new TreeMap<>(searchResult);
		}
	}
}
