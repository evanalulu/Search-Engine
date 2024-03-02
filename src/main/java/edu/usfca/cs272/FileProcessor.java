package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.text.html.HTMLDocument.Iterator;

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
                    try {
                        readFile(path, index);
                        Map<String, Integer> wordCountMap = index.getWordCountMap();
                        int wordCount = wordCountMap.get(path.toString());
	                    if (wordCount > 0) index.addCount(path.toString(), wordCount);
                    } catch (IOException e) {
                        // 
                    }
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
    		Stemmer stemmer = new SnowballStemmer(ENGLISH);
            while ((line = reader.readLine()) != null) {
                String[] words = FileStemmer.parse(line);
                wordCount += words.length;

                for (String word : words) {
                    String stemmedWord = FileStemmer.findStem(word, stemmer);
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
		
	public static TreeMap<String, ArrayList<IndexSearcher>> readQuery(Path path, InvertedIndex index) throws IOException {
		TreeMap<String, ArrayList<IndexSearcher>> result = new TreeMap<>();
		
        Set<TreeSet<String>> query = getQuery(path);
        System.out.println(query);
        for (TreeSet<String> querySet : query) {
        	performSearch(querySet, index, result);
        }
        
        return result;
	}
	
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
                if (!wordsString.isEmpty())
                	query.add(FileStemmer.uniqueStems(wordsString));
            }
        }
        return query;
	}

	private static void performSearch(TreeSet<String> query, InvertedIndex index, TreeMap<String, ArrayList<IndexSearcher>> result) {
		TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap = index.getIndexMap();
		String queryString = treeSetToString(query);
		System.out.print(queryString + ": " + query.size());

		for (String queryTerm : query) {
			boolean filePathPassed = false;
		    ArrayList<IndexSearcher> innerList = new ArrayList<>();
		    if (indexMap.containsKey(queryTerm)) {
		        TreeMap<String, ArrayList<Integer>> innerIndexMap = indexMap.get(queryTerm);
		        for (var entry : innerIndexMap.entrySet()) {
		            String path = entry.getKey();
		            ArrayList<Integer> value = entry.getValue();
		                                
		            if (result.containsKey(queryString) && filePathMatch(queryString, path, result)) {
		                ArrayList<IndexSearcher> check = result.get(queryString);
		                filePathPassed = true;
		                
		                java.util.Iterator<IndexSearcher> iterator = check.iterator();

		                while (iterator.hasNext()) {
		                    IndexSearcher currentSearcher = iterator.next();
		                    if (filePathMatch(currentSearcher, path)) {
		                        int totalMatches = value.size();
		                        currentSearcher.addCount(totalMatches);
		                        
		                        String score = calculateScore(index, path, currentSearcher.getCount());
		                        currentSearcher.setScore(score);
		                        
		                        innerList.add(currentSearcher);
		                    }
		                }
		            } else {
		                IndexSearcher searcher = new IndexSearcher(0, null, null);
		                
		                int totalMatches = value.size();
		                searcher.setCount(totalMatches);
		                
		                String score = calculateScore(index, path, totalMatches);
		                searcher.setScore(score);
		                
		                searcher.setWhere(Path.of(path));
		                
		                innerList.add(searcher);
		            }
		        }
		    }
		    if (result.containsKey(queryString) && !filePathPassed) {
		        ArrayList<IndexSearcher> existingSearchers = result.get(queryString);
		        existingSearchers.addAll(innerList);
		    } else {
		        result.put(queryString, innerList);
		    }
		}
	}
	
	private static boolean filePathMatch(String queryString, String path, TreeMap<String, ArrayList<IndexSearcher>> result) {
		ArrayList<IndexSearcher> check = result.get(queryString);
		java.util.Iterator<IndexSearcher> iterator = check.iterator();
		
		while (iterator.hasNext()) {
		    IndexSearcher currentSearcher = iterator.next();
		    if (currentSearcher.getWhere().toString().equalsIgnoreCase(path))
		    	return true;
		}
		return false;
	}
	
	
	private static boolean filePathMatch(IndexSearcher searcher, String path) {
		return (searcher.getWhere().toString().equalsIgnoreCase(path));
	}
	
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
    
    private static String calculateScore(InvertedIndex index, String path, int totalMatches) {
    	DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
    	
    	int totalWords = findTotalWords(index, path);
    	double score = (double) totalMatches/totalWords;
        String formattedScore = FORMATTER.format(score);
        return formattedScore;
    	
    }
    
    private static int findTotalWords(InvertedIndex index, String queryTerm) {
    	Map<String, Integer> wordCountMap = index.getWordCountMap();

    	if (wordCountMap.containsKey(queryTerm)) {
    		return wordCountMap.get(queryTerm);
    	}
    	return -1;
    }
}