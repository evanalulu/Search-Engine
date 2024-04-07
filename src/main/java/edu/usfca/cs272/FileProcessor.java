package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class responsible for processing files and directories. It includes methods
 * to traverse directories recursively and read the content of text files,
 *
 * @author Evana Pradhan
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class FileProcessor {
	/**
	 * Recursively traverses the specified directory and processes each file. For
	 * each regular file with a ".txt" extension, it reads the file and updates the
	 * inverted index. If a file path is already present in the inverted index, it
	 * updates the word count and index information.
	 *
	 * @param directory the directory to traverse
	 * @param index the inverted index to update
	 * @throws IOException if an I/O error occurs while traversing the directory or
	 *   reading files
	 */
	public static void traverseDirectory(Path directory, InvertedIndex index) throws IOException {
		try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory)) {
			for (Path path : paths) {
				if (Files.isDirectory(path)) {
					traverseDirectory(path, index);
				}
				else if (Files.isRegularFile(path) && isExtensionText(path)) {
					readFile(path, index);
				}
			}
		}
	}

	/**
	 * Reads the content of the specified file, parses it line by line, and updates
	 * the inverted index. For each line, it extracts words, stems them, and adds
	 * them to the inverted index along with their positions. It also updates the
	 * word count for the file in the inverted index.
	 *
	 * @param path the path to the file to read
	 * @param index the inverted index to update
	 * @throws IOException if an I/O error occurs while reading the file
	 */
	public static void readFile(Path path, InvertedIndex index) throws IOException {
		int wordCount = 0;

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			int position = 1;
			String pathStr = path.toString();
			Stemmer stemmer = new SnowballStemmer(ENGLISH);

			while ((line = reader.readLine()) != null) {
				String[] words = FileStemmer.parse(line);
				wordCount += words.length;

				for (String word : words) {
					String stemmedWord = stemmer.stem(word).toString();
					index.addWord(stemmedWord, pathStr, position);
					position++;
				}
			}

			index.addCount(pathStr, wordCount);
		}
	}

	/**
	 * Processes the given input path, updating the provided inverted index.
	 *
	 * @param input the path to a directory or file
	 * @param index the inverted index to update
	 * @throws IOException if an I/O error occurs
	 */
	public static void processPath(Path input, InvertedIndex index) throws IOException {
		if (Files.isDirectory(input)) {
			FileProcessor.traverseDirectory(input, index);
		}
		else {
			FileProcessor.readFile(input, index);
		}
	}

	/**
	 * Checks if the file extension of the specified path corresponds to a text
	 * file.
	 *
	 * @param path the path of the file to check
	 * @return {@code true} if the file extension is ".txt" or ".text",
	 *   {@code false} otherwise
	 */
	public static boolean isExtensionText(Path path) {
		String extension = path.toString().toLowerCase();
		return extension.endsWith(".txt") || extension.endsWith(".text");
	}

	/**
	 * Reads queries from a file and performs search on an inverted index.
	 * 
	 * @param path The path to the file containing queries.
	 * @param index The inverted index to perform searches on.
	 * @param isPartial If -partial search is requested
	 * @return A TreeMap where each query term maps to a list of IndexSearchers
	 *   containing search results.
	 * @throws IOException If an I/O error occurs while reading the query file.
	 */
	public static TreeMap<String, ArrayList<IndexSearcher>> readQuery(Path path, InvertedIndex index, Boolean isPartial)
			throws IOException {
		TreeMap<String, ArrayList<IndexSearcher>> result = new TreeMap<>();

		Set<TreeSet<String>> query = getQuery(path);
		for (TreeSet<String> querySet : query) {
			if (isPartial) {
				partialSearch(querySet, index, result);
			}
			else {
				exactSearch(querySet, index, result);
			}
		}

		return result;
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
	public static Set<TreeSet<String>> getQuery(Path path) throws IOException {
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

	/**
	 * Performs exact search based on the provided query, updating the result map
	 * with search results.
	 *
	 * @param query The query terms to search for.
	 * @param index The inverted index to search within.
	 * @param result The map to store the search results, where each query term maps
	 *   to a list of IndexSearchers.
	 */
	private static void exactSearch(TreeSet<String> query, InvertedIndex index,
			TreeMap<String, ArrayList<IndexSearcher>> result) {

		for (String queryTerm : query) {
			String queryString = treeSetToString(query);
			ArrayList<IndexSearcher> innerList = new ArrayList<>();

			if (index.hasWord(queryTerm)) {
				Set<String> locations = index.viewLocations(queryTerm);

				for (String path : locations) {
					Set<Integer> value = index.viewPositions(queryTerm, path);
					calculateResult(result, queryString, index, path, value);
				}
			}

			result.computeIfAbsent(queryString, k -> new ArrayList<>()).addAll(innerList);
			Collections.sort(result.get(queryString));
		}
	}

	/**
	 * Calculates and updates the search result based on the query string, inverted
	 * index, path, and set of matching positions. Updates the provided result map
	 * with the calculated information.
	 *
	 * @param result the map to store the search results
	 * @param queryString the query string used for the search
	 * @param index the inverted index used for the search
	 * @param path the path of the file being searched
	 * @param value the set of matching positions within the file
	 */
	public static void calculateResult(TreeMap<String, ArrayList<IndexSearcher>> result, String queryString,
			InvertedIndex index, String path, Set<Integer> value) {
		ArrayList<IndexSearcher> searchers = result.computeIfAbsent(queryString, k -> new ArrayList<>());
		int totalMatches = value.size();

		IndexSearcher existingSearcher = findSearcherForPath(searchers, path);
		if (existingSearcher != null) {
			existingSearcher.addCount(totalMatches);
			existingSearcher.setScore(calculateScore(index, path, existingSearcher.getCount()));
		}
		else {
			String score = calculateScore(index, path, totalMatches);
			IndexSearcher newSearcher = new IndexSearcher(totalMatches, score, Path.of(path));
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
	 * @param index The inverted index to search within.
	 * @param result The map to store the search results, where each query term maps
	 *   to a list of IndexSearchers.
	 */
	private static void partialSearch(TreeSet<String> query, InvertedIndex index,
			TreeMap<String, ArrayList<IndexSearcher>> result) {
		String queryString = treeSetToString(query);

		for (String queryTerm : query) {
			ArrayList<IndexSearcher> termResults = new ArrayList<>();

			for (String word : index.viewWords()) {
				if (word.startsWith(queryTerm)) {
					for (String location : index.viewLocations(word)) {
						Set<Integer> positions = index.viewPositions(word, location);
						updateSearchResults(termResults, location, positions, index);
					}
				}
			}

			mergeResults(result.computeIfAbsent(queryString, k -> new ArrayList<>()), termResults, index);
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
	 * @param index the inverted index used for score calculation
	 */
	private static void updateSearchResults(ArrayList<IndexSearcher> searchers, String location, Set<Integer> positions,
			InvertedIndex index) {
		IndexSearcher searcher = findOrCreateSearcher(searchers, location);
		searcher.addCount(positions.size());
		searcher.setScore(calculateScore(index, location, searcher.getCount()));
	}

	/**
	 * Finds an existing IndexSearcher for the given location from the provided list
	 * of searchers, or creates a new one if not found.
	 *
	 * @param searchers the list of searchers to search within
	 * @param location the location to match against existing searchers
	 * @return the existing or newly created IndexSearcher
	 */
	private static IndexSearcher findOrCreateSearcher(ArrayList<IndexSearcher> searchers, String location) {
		for (IndexSearcher searcher : searchers) {
			if (searcher.getWhere().equals(location)) {
				return searcher;
			}
		}
		IndexSearcher newSearcher = new IndexSearcher(0, "0", Path.of(location));
		searchers.add(newSearcher);
		return newSearcher;
	}

	/**
	 * Merges the results of query term searches into main results and sorts them.
	 *
	 * @param mainResults The main list of search results.
	 * @param termResults The list of search results from a term search.
	 * @param index The InvertedIndex containing the indexed data.
	 */
	private static void mergeResults(ArrayList<IndexSearcher> mainResults, ArrayList<IndexSearcher> termResults,
			InvertedIndex index) {
		for (IndexSearcher termSearcher : termResults) {
			boolean found = false;
			for (IndexSearcher mainSearcher : mainResults) {
				if (mainSearcher.getWhere().equals(termSearcher.getWhere())) {
					mainSearcher.addCount(termSearcher.getCount());
					mainSearcher.setScore(calculateScore(index, termSearcher.getWhere().toString(), mainSearcher.getCount()));
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
	 * @param index The inverted index containing word count information.
	 * @param path The path of the document to calculate the score for.
	 * @param totalMatches The total number of matches for the query term in the
	 *   document.
	 * @return The calculated score as a formatted string.
	 */
	private static String calculateScore(InvertedIndex index, String path, int totalMatches) {
		DecimalFormat FORMATTER = new DecimalFormat("0.00000000");

		int totalWords = findTotalWords(index, path);
		double score = (double) totalMatches / totalWords;
		String formattedScore = FORMATTER.format(score);
		return formattedScore;
	}

	/**
	 * Finds the total words in a path
	 * 
	 * @param index The inverted index containing word count information.
	 * @param path The path of the document to count the words for.
	 * @return The total words in path.
	 */
	private static int findTotalWords(InvertedIndex index, String path) {
		return index.getWordCount(path);
	}

	/**
	 * Converts the elements of a TreeSet into a single string using
	 * {@link StringBuilder}.
	 *
	 * @param treeSet The TreeSet to convert into a string.
	 * @return A string representation of the TreeSet elements.
	 */
	private static String treeSetToString(TreeSet<String> treeSet) {
		StringBuilder sb = new StringBuilder();
		for (String element : treeSet) {
			sb.append(element).append(" ");
		}
		if (!treeSet.isEmpty()) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
}
