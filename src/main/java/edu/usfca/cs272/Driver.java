package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
	 */
	public static void main(String[] args) {
		
		ArgumentParser argsMap = new ArgumentParser(args);
								        
		String input = argsMap.hasFlag("-text") ? argsMap.getString("-text") : null;
		System.out.println(input);
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
    		Pair<Integer, TreeMap<String, TreeMap<String, ArrayList<Integer>>>> res = readInput(input);
    		
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
	
	private static void traverseDirectory(Path directory, Map<String, Integer> wordCountMap, TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap) {
		try (Stream<Path> paths = Files.list(directory)) {
	        paths.forEach(path -> {
	            if (Files.isDirectory(path)) {
	                traverseDirectory(path, wordCountMap, indexMap);
	            } else if (Files.isRegularFile(path)) {
	                String pathString = path.toString();
	                String extension = pathString.substring(pathString.lastIndexOf('.') + 1);
	                
	                if ((extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("text"))) {
	                    Pair<Integer, TreeMap<String, TreeMap<String, ArrayList<Integer>>>> res = readInput(pathString);
	                    int wordCount = res.getLeft();
	                    if (wordCount > 0) wordCountMap.put(pathString, wordCount);
	                    
	                    TreeMap<String, TreeMap<String, ArrayList<Integer>>> index = res.getRight();
	                    
	                    for (Map.Entry<String, TreeMap<String, ArrayList<Integer>>> entry : index.entrySet()) { 
	                        String key = entry.getKey();
	                        TreeMap<String, ArrayList<Integer>> value = entry.getValue();
	                        
	                        String filePath = value.keySet().iterator().next();
	                        ArrayList<Integer> indices = value.get(filePath); 
	                        
	                        if (!indexMap.containsKey(key)) {
	                        	TreeMap<String, ArrayList<Integer>> temp = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	                        	indexMap.put(key, temp);
	                        	indexMap.get(key).put(filePath, indices);
	                        } else {
	                        	indexMap.get(key).put(filePath, indices);
	                        }
	                        
	                    } 
	

	                }


	            }

	        });
	    } catch (IOException e) {
	        System.err.println("Error traversing directory: " + e.getMessage());
	    }
	}

	private static Pair<Integer, TreeMap<String, TreeMap<String, ArrayList<Integer>>>> readInput(String filePath) {
	    int wordCount = 0;
	    TreeMap<String, TreeMap<String, ArrayList<Integer>>> wordPositionsMap = new TreeMap<>();

	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	        String line;
	        int position = 1;
	        while ((line = reader.readLine()) != null) {
	            String clean = FileStemmer.clean(line);
	            String[] split = FileStemmer.split(clean);
	            wordCount += split.length;

	            for (String word : split) {
	                TreeSet<String> stemmed = FileStemmer.uniqueStems(word);
	                String stemmedWord = stemmed.first();
	                wordPositionsMap.computeIfAbsent(stemmedWord, k -> new TreeMap<>())
	                        .computeIfAbsent(filePath, k -> new ArrayList<>()).add(position);
	                position++;
	            }

	        }
	    } catch (IOException e) {
	        System.err.println("Error reading file: " + e.getMessage());
	    }

	    if (wordCount == 0)
	        return Pair.of(wordCount, new TreeMap<>());

	    return Pair.of(wordCount, wordPositionsMap);
	}

    private static void writeFile(String filePath, String res) {
        try (Writer writer = new FileWriter(filePath)) {
            writer.write(res);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private static void printFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            System.out.println(filePath);
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
	
}
