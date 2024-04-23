package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
	public static TreeMap<String, ArrayList<InvertedIndex.IndexSearcher>> readQuery(Path path, InvertedIndex index,
			Boolean isPartial) throws IOException {
		TreeMap<String, ArrayList<InvertedIndex.IndexSearcher>> result = new TreeMap<>();

		Set<TreeSet<String>> query = getQuery(path);
		for (TreeSet<String> querySet : query) {
			if (isPartial) {
				index.partialSearch(querySet);
			}
			else {
				index.exactSearch(querySet);
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
	public static Set<TreeSet<String>> getQuery(Path path) throws IOException { // TODO Remove
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
