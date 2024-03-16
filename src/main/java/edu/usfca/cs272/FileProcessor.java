package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class responsible for processing files and directories.
 * It includes methods to traverse directories recursively and read the content of text files,
 *
 * @author Evana Pradhan
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class FileProcessor {
	/**
	 * Recursively traverses the specified directory and processes each file.
	 * For each regular file with a ".txt" extension, it reads the file and updates the inverted index.
	 * If a file path is already present in the inverted index, it updates the word count and index information.
	 *
	 * @param directory the directory to traverse
	 * @param index the inverted index to update
	 * @throws IOException if an I/O error occurs while traversing the directory or reading files
	 */
	public static void traverseDirectory(Path directory, InvertedIndex index) throws IOException {
		try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory)) {
			for (Path path : paths) {
				if (Files.isDirectory(path)) {
					traverseDirectory(path, index);
				} else if (Files.isRegularFile(path) && isExtensionText(path)) {
					readFile(path, index);
				}
			}
		}
	}

	/**
	 * Reads the content of the specified file, parses it line by line, and updates the inverted index.
	 * For each line, it extracts words, stems them, and adds them to the inverted index along with their positions.
	 * It also updates the word count for the file in the inverted index.
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
	 * Checks if the file extension of the specified path corresponds to a text file.
	 *
	 * @param path the path of the file to check
	 * @return {@code true} if the file extension is ".txt" or ".text", {@code false} otherwise
	 */
	public static boolean isExtensionText(Path path) {
		String extension = path.toString().toLowerCase();
		return extension.endsWith(".txt") || extension.endsWith(".text");
	}
}