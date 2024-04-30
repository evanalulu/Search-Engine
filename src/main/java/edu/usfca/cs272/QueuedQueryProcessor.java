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
			ArrayList<InvertedIndex.IndexSearcher> results = index.search(querySet, isPartial);
			searchResult.put(String.join(" ", querySet), results);
		}
	}

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
}
