package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
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

	/**
	 * Returns the number of results in result map.
	 *
	 * @return the number of results in result map
	 */
	public int getResultCount() {
		return searchResult.size();
	}

	/**
	 * Retrieves an unmodifiable view of the search result map.
	 *
	 * @return An unmodifiable map where keys are query strings and values are lists
	 *   of IndexSearcher objects, representing the search results for each query.
	 */
	public Map<String, ArrayList<IndexSearcher>> viewResult() {
		return Collections.unmodifiableMap(searchResult);
	}

	/**
	 * Retrieves an unmodifiable set of words present in the search result map.
	 *
	 * @return an unmodifiable set containing the unique words for which search
	 *   results are available
	 */
	public Set<String> viewWords() {
		return Collections.unmodifiableSet(searchResult.keySet());
	}

	/**
	 * Retrieves an unmodifiable collection of lists of IndexSearcher objects
	 * representing search results.
	 *
	 * @return an unmodifiable collection containing lists of IndexSearcher objects,
	 *   representing search results for each word
	 */
	public Collection<ArrayList<IndexSearcher>> viewSearchers() {
		return Collections.unmodifiableCollection(searchResult.values());
	}

	/**
	 * Checks if a specific word is indexed in search results.
	 *
	 * @param word the word to check
	 * @return {@code true} if the word is indexed
	 */
	public boolean hasWord(String word) {
		return searchResult.containsKey(word);
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
				partialSearch(querySet);
			}
			else {
				exactSearch(querySet);
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

	// Putting search functions in QueryProcessor instead of InvertedIndex for easy
	// access/encapsulation of results

	/**
	 * Performs exact search based on the provided query, updating the result map
	 * with search results.
	 *
	 * @param query The query terms to search for.
	 */
	public void exactSearch(TreeSet<String> query) {
		for (String queryTerm : query) {
			String queryString = treeSetToString(query);
			ArrayList<IndexSearcher> innerList = searchResult.getOrDefault(queryString, new ArrayList<>());

			if (index.hasWord(queryTerm)) {
				Set<String> locations = index.viewLocations(queryTerm);

				for (String path : locations) {
					Set<Integer> value = index.viewPositions(queryTerm, path);
					calculateResult(queryString, path, value);
				}
			}

			if (!searchResult.containsKey(queryString)) {
				searchResult.put(queryString, innerList);
			}

			Collections.sort(searchResult.get(queryString));
		}
	}

	/**
	 * Calculates and updates the search result based on the query string, inverted
	 * index, path, and set of matching positions. Updates the provided result map
	 * with the calculated information.
	 *
	 * @param queryString the query string used for the search
	 * @param path the path of the file being searched
	 * @param value the set of matching positions within the file
	 */
	public void calculateResult(String queryString, String path, Set<Integer> value) {
		ArrayList<IndexSearcher> searchers = searchResult.computeIfAbsent(queryString, k -> new ArrayList<>());
		int totalMatches = value.size();

		IndexSearcher existingSearcher = findSearcherForPath(searchers, path);
		if (existingSearcher != null) {
			existingSearcher.addCount(totalMatches);
			existingSearcher.setScore(calculateScore(path, existingSearcher.getCount()));
		}
		else {
			Double score = calculateScore(path, totalMatches);
			IndexSearcher newSearcher = index.new IndexSearcher(totalMatches, score, path);
			searchers.add(newSearcher);
		}
	}

	/**
	 * Finds an existing IndexSearcher for the given path from the provided list of
	 * searchers.
	 *
	 * @param searchers the list of searchers to search within
	 * @param path the path to match against existing searchers
	 * @return the existing IndexSearcher if found, otherwise null
	 */
	private static IndexSearcher findSearcherForPath(ArrayList<IndexSearcher> searchers, String path) {
		for (IndexSearcher searcher : searchers) {
			if (filePathMatch(searcher, path)) {
				return searcher;
			}
		}
		return null;
	}

	/**
	 * Performs partial search based on the provided query, updating the result map
	 * with search results.
	 * 
	 * @param query The query terms to search for.
	 */
	public void partialSearch(TreeSet<String> query) {
		String queryString = treeSetToString(query);

		for (String queryTerm : query) {
			ArrayList<IndexSearcher> termResults = new ArrayList<>();

			for (String word : index.viewWords()) {
				if (word.startsWith(queryTerm)) {
					for (String location : index.viewLocations(word)) {
						Set<Integer> positions = index.viewPositions(word, location);
						updateSearchResults(termResults, location, positions);
					}
				}
			}

			mergeResults(searchResult.computeIfAbsent(queryString, k -> new ArrayList<>()), termResults);
		}
	}

	/**
	 * Updates the search results for a specific location with the provided set of
	 * positions. If an IndexSearcher for the location already exists in the list of
	 * searchers, it updates its count and score. Otherwise, a new IndexSearcher is
	 * created for the location.
	 *
	 * @param searchers the list of searchers to update or add to
	 * @param location the location of the matched word positions
	 * @param positions the set of positions where the word is found in the location
	 */
	private void updateSearchResults(ArrayList<IndexSearcher> searchers, String location, Set<Integer> positions) {
		IndexSearcher searcher = findOrCreateSearcher(searchers, location);
		searcher.addCount(positions.size());
		searcher.setScore(calculateScore(location, searcher.getCount()));
	}

	/**
	 * Finds an existing IndexSearcher for the given location from the provided list
	 * of searchers, or creates a new one if not found.
	 * 
	 * @param searchers the list of searchers to search within
	 * @param location the location to match against existing searchers
	 * @return the existing or newly created IndexSearcher
	 */
	private IndexSearcher findOrCreateSearcher(ArrayList<IndexSearcher> searchers, String location) {
		for (IndexSearcher searcher : searchers) {
			if (searcher.getWhere().equals(location)) {
				return searcher;
			}
		}
		IndexSearcher newSearcher = index.new IndexSearcher(0, 0.0, location);
		searchers.add(newSearcher);
		return newSearcher;
	}

	/**
	 * Merges the results of query term searches into main results and sorts them.
	 *
	 * @param mainResults The main list of search results.
	 * @param termResults The list of search results from a term search.
	 */
	private void mergeResults(ArrayList<IndexSearcher> mainResults, ArrayList<IndexSearcher> termResults) {
		for (IndexSearcher termSearcher : termResults) {
			boolean found = false;
			for (IndexSearcher mainSearcher : mainResults) {
				if (mainSearcher.getWhere().equals(termSearcher.getWhere())) {
					mainSearcher.addCount(termSearcher.getCount());
					mainSearcher.setScore(calculateScore(termSearcher.getWhere(), mainSearcher.getCount()));
					found = true;
					break;
				}
			}
			if (!found) {
				mainResults.add(termSearcher);
			}
		}
		Collections.sort(mainResults);
	}

	/**
	 * Checks if the file path in the given IndexSearcher matches the specified
	 * path.
	 *
	 * @param searcher The IndexSearcher object containing the file path to compare.
	 * @param path The file path to compare against.
	 * @return {@code true} if the file path in the IndexSearcher matches the
	 *   specified path, {@code false} otherwise.
	 */
	private static boolean filePathMatch(IndexSearcher searcher, String path) {
		return (searcher.getWhere().toString().equalsIgnoreCase(path));
	}

	/**
	 * Calculates the score for a given search result based on the total number of
	 * matches and total words in the document.
	 *
	 * @param path The path of the document to calculate the score for.
	 * @param totalMatches The total number of matches for the query term in the
	 *   document.
	 * @return The calculated score as a formatted string.
	 */
	private Double calculateScore(String path, int totalMatches) {

		int totalWords = index.getWordCount(path);
		return (double) totalMatches / totalWords;

	}

	/**
	 * Converts the elements of a TreeSet into a single string using
	 * {@link StringBuilder}.
	 *
	 * @param treeSet The TreeSet to convert into a string.
	 * @return A string representation of the TreeSet elements.
	 */
	private static String treeSetToString(Set<String> treeSet) {
		return String.join(" ", treeSet);
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

}
