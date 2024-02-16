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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
//		if (argsMap.empty())
//			System.err.println("Error: No arguments provided.");
								        
        String input = argsMap.getString("-text");
        String countOutput = null;
        String indexOutput = null;

        if (argsMap.hasFlag("-counts"))
            countOutput = (argsMap.hasValue("-counts")) ? argsMap.getString("-counts") : "counts.json";
        
        if (argsMap.hasFlag("-index"))
        	indexOutput = (argsMap.hasValue("-index")) ? argsMap.getString("-index") : "index.json";

        Pair<Integer, Map<String, Map<Path, List<Integer>>>> res = readInput(input);
        int wordCount = res.getLeft();
        Map<String, Map<Path, List<Integer>>> wordPositionsMap = res.getRight();
        TreeMap<String, Map<Path, List<Integer>>> sortedWordPositionsMap = new TreeMap<>(wordPositionsMap);
        
        String outputMap = JsonWriter.writeWordPositionsMap(sortedWordPositionsMap);

        writeFile(indexOutput, outputMap);
        printFile(indexOutput);
        
        	
        
//    	if(input != null && Files.isDirectory(Paths.get(input))) {
//    		Map<String, Integer> wordCountMap = new TreeMap<>();
//            traverseDirectory(Paths.get(input), wordCountMap);
//            String res = JsonWriter.writeObject(wordCountMap);
//            writeFile(countOutput, res);
//    	} else if (input != null && countOutput != null) {
//            int wordCount = countWords(input);
//            if (wordCount > 0) {
//            	String countRes = JsonWriter.writeObject(Map.of(input, wordCount));
//            	writeFile(countOutput, countRes);
//            	String indexRes = JsonWriter.writeObject(Collections.emptyMap());
//                writeFile(indexOutput, indexRes);
//            } else {
//            	String res = JsonWriter.writeObject(Collections.emptyMap());
//            	writeFile(countOutput, res);
//            	String indexRes = JsonWriter.writeObject(Collections.emptyMap());
//                writeFile(indexOutput, indexRes);
//            }
//    	} else if (input == null && countOutput != null) {
//    		String res = JsonWriter.writeObject(Collections.emptyMap());
//        	writeFile(countOutput, res);
//        	String indexRes = JsonWriter.writeObject(Collections.emptyMap());
//            writeFile(indexOutput, res);
//    	}
	}
	
//    private static void traverseDirectory(Path directory, Map<String, Integer> wordCountMap) {
//        try (Stream<Path> paths = Files.list(directory)) {
//            paths.forEach(path -> {
//                if (Files.isDirectory(path)) {
//                    traverseDirectory(path, wordCountMap);
//                } else if (Files.isRegularFile(path)) {
//                	String pathString = path.toString();
//                	String extension = pathString.substring(pathString.lastIndexOf('.') + 1);
//                	if (extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("text")) {
//                		int wordCount = countWords(pathString);
//                		if (wordCount > 0)
//                			wordCountMap.put(path.toString(), wordCount);
//                	}
//                }
//            });
//        } catch (IOException e) {
//            System.err.println("Error traversing directory: " + e.getMessage());
//        }
//    }

    private static Pair<Integer, Map<String, Map<Path, List<Integer>>>> readInput(String filePath) {
        int wordCount = 0;
        Map<String, Map<Path, List<Integer>>> wordPositionsMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int position = 1;
            while ((line = reader.readLine()) != null) {
                String clean = FileStemmer.clean(line);
                String[] split = FileStemmer.split(clean);

                for (String word : split) {
                    wordPositionsMap.computeIfAbsent(word, k -> new HashMap<>())
                            .computeIfAbsent(Paths.get(filePath), k -> new ArrayList<>()).add(position);
                    position++;
                }

                wordCount += split.length;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

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
