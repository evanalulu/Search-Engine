package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.usfca.cs272.InvertedIndex.IndexSearcher;

public class QueryProcessor {

	private InvertedIndex index;
	private TreeMap<String, ArrayList<InvertedIndex.IndexSearcher>> searchResult;

	public QueryProcessor(InvertedIndex index) {
		this.index = index;
		this.searchResult = new TreeMap<>();
	}

	public Map<String, ArrayList<IndexSearcher>> viewResult() {
		return Collections.unmodifiableMap(searchResult);
	}

	/**
	 * Reads queries from a file and performs search on an inverted index.
	 * 
	 * @param path The path to the file containing queries.
	 * @param isPartial If -partial search is requested
	 * @throws IOException If an I/O error occurs while reading the query file.
	 */
	public void processQueries(Path path, Boolean isPartial) throws IOException {

		Set<TreeSet<String>> query = getQuery(path);
		for (TreeSet<String> querySet : query) {
			if (isPartial) {
				index.partialSearch(querySet, searchResult);
			}
			else {
				index.exactSearch(querySet, searchResult);
			}
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

}
