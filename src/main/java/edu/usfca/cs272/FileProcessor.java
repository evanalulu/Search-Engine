package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

/*
 * TODO 

Create 1 new "InvertedIndex" data structure class:

Map<String, Integer> wordCountMap 
TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap

at a minimum, add methods:

addCount(String location, Integer count) --> wordCountMap
addWord(String word, String location, Integer position) --> indexMap

 */

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
	 * Recursively traverses the specified directory, processing each file.
	 *
	 * @param directory   the directory to traverse
	 * @param wordCountMap   a map to store word counts for each file
	 * @param indexMap   a map to store word positions for each word in each file
	 * @throws IOException if an I/O error occurs while traversing the directory or processing files
	 */
	public static void traverseDirectory(Path directory, InvertedIndex index) throws IOException {
		
	}
	
//	public static void traverseDirectory(Path directory, Map<String, Integer> wordCountMap, TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap) throws IOException{
//		try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory)) {
//			paths.forEach(path -> { // TODO for (Path path : paths)
//				if (Files.isDirectory(path)) {
//					try { // TODO Remove try/catch entirely
//						traverseDirectory(path, wordCountMap, indexMap);
//					} catch (IOException e) {
//					}
//				} else if (Files.isRegularFile(path) && isExtensionText(path)) {
//                	Pair<Integer, TreeMap<String, TreeMap<String, ArrayList<Integer>>>> maps;
//					try {
//						maps = readFile(path);
//						// TODO SHould only need the readFile call here
//	                    int wordCount = maps.getLeft();
//	                    if (wordCount > 0) 
//	                    	wordCountMap.put(path.toString(), wordCount);
//	                    
//	                    TreeMap<String, TreeMap<String, ArrayList<Integer>>> index = maps.getRight();
//	                    
//	                    // TODO Use for loop
//	                    index.forEach((word, filePathToIndicesMap) ->
//	                    filePathToIndicesMap.forEach((filePath, indices) ->
//	                        indexMap.computeIfAbsent(word, k -> new TreeMap<>())
//	                                .put(filePath, new ArrayList<>(indices))
//	                    	)
//	                    );
//					} catch (IOException e) {
//						// TODO Clue you are catching in the wrong place
//					}
//				}
//			});
//		}
//	}

	/**
	 * Reads the content of the file located at the specified path, processes the words, and returns
	 * the word count along with a map of word positions.
	 *
	 * @param path the path to the file to be read
	 * @return a pair containing the word count and a map of word positions
	 * @throws IOException if an I/O error occurs while reading the file
	 */
	public static InvertedIndex readFile(Path path, InvertedIndex index) throws IOException {
		int wordCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int position = 1;

            while ((line = reader.readLine()) != null) {
                String[] words = FileStemmer.parse(line);
                wordCount += words.length;

                for (String word : words) {
                    String stemmedWord = FileStemmer.uniqueStems(word).first(); // TODO stemmer.stem(word).toString()

                    index.addWord(stemmedWord, path.toString(), position);
                    position++;
                }
            }
        }

        if (wordCount == 0) {
            return new InvertedIndex();
        } else {
            return index;
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
