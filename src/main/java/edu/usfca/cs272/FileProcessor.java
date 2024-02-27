package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

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
	 * Recursively traverses the specified directory, processing each file.
	 *
	 * @param directory   the directory to traverse
	 * @param wordCountMap   a map to store word counts for each file
	 * @param indexMap   a map to store word positions for each word in each file
	 * @throws IOException if an I/O error occurs while traversing the directory or processing files
	 */
	public static void traverseDirectory(Path directory, InvertedIndex index) throws IOException {
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory)) {
            for (Path path : paths) {
                if (Files.isDirectory(path)) {
                    traverseDirectory(path, index);
                } else if (Files.isRegularFile(path) && isExtensionText(path)) {
                    try {
                        readFile(path, index);
                        Map<String, Integer> wordCountMap = index.getWordCountMap();
                        int wordCount = wordCountMap.get(path.toString());
	                    if (wordCount > 0) index.addCount(path.toString(), wordCount);
	                    
                        TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap = index.getIndexMap();

                        for (Map.Entry<String, TreeMap<String, ArrayList<Integer>>> entry : indexMap.entrySet()) { 
	                        String key = entry.getKey();
	                        TreeMap<String, ArrayList<Integer>> value = entry.getValue();
	                        
	                        String filePath = value.keySet().iterator().next();
	                        ArrayList<Integer> indices = value.get(filePath); 
	                        
	                        if (!indexMap.containsKey(key)) {
	                        	TreeMap<String, ArrayList<Integer>> temp = new TreeMap<>();
	                        	indexMap.put(key, temp);
	                        	indexMap.get(key).put(filePath, indices);
	                        } else {
	                        	indexMap.get(key).put(filePath, indices);
	                        }
	                        
	                    } 
                    } catch (IOException e) {
                        // 
                    }
                }
            }
        }
    }

	/**
	 * Reads the content of the file located at the specified path, processes the words, and returns
	 * the word count along with a map of word positions.
	 *
	 * @param path the path to the file to be read
	 * @return a pair containing the word count and a map of word positions
	 * @throws IOException if an I/O error occurs while reading the file
	 */
	public static void readFile(Path path, InvertedIndex index) throws IOException {
		int wordCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int position = 1;
    		Stemmer stemmer = new SnowballStemmer(ENGLISH);
            while ((line = reader.readLine()) != null) {
                String[] words = FileStemmer.parse(line);
                wordCount += words.length;

                for (String word : words) {
                    String stemmedWord = stemmer.stem(word).toString();

                    index.addWord(stemmedWord, path.toString(), position);
                    position++;
                }
            }
            index.addCount(path.toString(), wordCount);
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
