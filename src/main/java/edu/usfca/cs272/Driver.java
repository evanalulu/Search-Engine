package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Evana Pradhan
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		ArgumentParser argsMap = new ArgumentParser(args);
								        
		String input = argsMap.hasFlag("-text") ? argsMap.getString("-text") : null;
        String countOutput = null;
        String indexOutput = null;

        if (argsMap.hasFlag("-counts"))
            countOutput = (argsMap.hasValue("-counts")) ? argsMap.getString("-counts") : "counts.json";
        
        if (argsMap.hasFlag("-index"))
        	indexOutput = (argsMap.hasValue("-index")) ? argsMap.getString("-index") : "index.json";
                
    	if(input != null && Files.isDirectory(Paths.get(input))) {
    		Map<String, Integer> wordCountMap = new TreeMap<>();
    		TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap = new TreeMap<>();
    		
            traverseDirectory(Paths.get(input), wordCountMap, indexMap);  
            
            String wordCountMap_JSON = JsonWriter.writeObject(wordCountMap);
            String indexMap_JSON = JsonWriter.writeWordPositionsMap(indexMap);
            if (indexOutput != null) writeFile(indexOutput, indexMap_JSON);
            if (countOutput != null) writeFile(countOutput, wordCountMap_JSON);
    	} else if (input == null) {
        	String wordCountMap_JSON = JsonWriter.writeObject(Collections.emptyMap());
            if (countOutput != null) writeFile(countOutput, wordCountMap_JSON);
        	String indexMap_JSON = JsonWriter.writeObject(Collections.emptyMap());
        	if (indexOutput != null) writeFile(indexOutput, indexMap_JSON);
		} else {
    		Pair<Integer, TreeMap<String, TreeMap<String, ArrayList<Integer>>>> res = readFile(Path.of(input));
    		
            int wordCount = res.getLeft();
            String wordCountMap = JsonWriter.writeObject(Map.of(input, wordCount));
            if (wordCount < 1) wordCountMap =  JsonWriter.writeObject(Collections.emptyMap());
            if (countOutput != null) writeFile(countOutput, wordCountMap);

            TreeMap<String, TreeMap<String, ArrayList<Integer>>> index = res.getRight();
            String indexMap = JsonWriter.writeWordPositionsMap(index);
            if (indexMap.length() == 2) indexMap = JsonWriter.writeObject(Collections.emptyMap());            
            if (indexOutput != null) writeFile(indexOutput, indexMap);
    	}
	}
	
	
	private static void traverseDirectory(Path directory, Map<String, Integer> wordCountMap, TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap) throws IOException{
		
		try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory)) {
			paths.forEach(path -> {
				if (Files.isDirectory(path)) {
					try {
						traverseDirectory(path, wordCountMap, indexMap);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (Files.isRegularFile(path) && isExtensionText(path)) {
                	Pair<Integer, TreeMap<String, TreeMap<String, ArrayList<Integer>>>> maps;
					try {
						maps = readFile(path);
	                    int wordCount = maps.getLeft();
	                    if (wordCount > 0) 
	                    	wordCountMap.put(path.toString(), wordCount);
	                    
	                    TreeMap<String, TreeMap<String, ArrayList<Integer>>> index = maps.getRight();
	                    
	                    index.forEach((word, filePathToIndicesMap) ->
	                    filePathToIndicesMap.forEach((filePath, indices) ->
	                        indexMap.computeIfAbsent(word, k -> new TreeMap<>())
	                                .put(filePath, new ArrayList<>(indices))
	                    	)
	                    );
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	private static Pair<Integer, TreeMap<String, TreeMap<String, ArrayList<Integer>>>> readFile(Path path) throws IOException {
	    int wordCount = 0;
	    TreeMap<String, TreeMap<String, ArrayList<Integer>>> wordPositionsMap = new TreeMap<>();

	    try (BufferedReader reader = Files.newBufferedReader(path, UTF_8)) {
	        String line;
	        int position = 1;
	        while ((line = reader.readLine()) != null) {
	            String[] words = FileStemmer.parse(line);
	            wordCount += words.length;

	            for (String word : words) {
	                String stemmedWord =  FileStemmer.uniqueStems(word).first();
	                wordPositionsMap.computeIfAbsent(stemmedWord, k -> new TreeMap<>())
	                        .computeIfAbsent(path.toString(), k -> new ArrayList<>()).add(position);
	                position++;
	            }
	        }
	    }

	    return (wordCount == 0) ? Pair.of(wordCount, new TreeMap<>()) : Pair.of(wordCount, wordPositionsMap);
	}

    private static void writeFile(String filePath, String res) {
        try (Writer writer = new FileWriter(filePath)) {
            writer.write(res);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private static boolean isExtensionText(Path path) {
	    String extension = path.toString().toLowerCase();
	    return extension.endsWith(".txt") || extension.endsWith(".text");
	}
}
