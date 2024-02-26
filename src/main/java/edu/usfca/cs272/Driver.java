package edu.usfca.cs272;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair; // TODO Try to remove

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
    public static void main(String[] args) { // TODO Don't throw here
    	/* TODO 
    	InvertedIndex index = new InvertedIndex();	
    	*/

        ArgumentParser parser = new ArgumentParser(args);
        
        Path input;
        Path countOutput;
        Path indexOutput;
        
        if (parser.hasFlag("-text")) {
		    input = parser.getPath("-text");
		}
        
        if (parser.hasFlag("-counts")) {
            countOutput = parser.getPath("-counts", Path.of("counts.json"));
        }

    	if (parser.hasFlag("-index")) {
		    indexOutput = parser.getPath("-index", Path.of("index.json"));
		}

    	/*
        if (input == null) {
            String emptyMap_JSON = JsonWriter.writeObject(Collections.emptyMap());
            if (countOutput != null) {
							writeFile(countOutput, emptyMap_JSON);
						}
            if (indexOutput != null) {
							writeFile(indexOutput, emptyMap_JSON);
						}
        } else if (Files.isDirectory(Paths.get(input))) {
            Map<String, Integer> wordCountMap = new TreeMap<>();
            TreeMap<String, TreeMap<String, ArrayList<Integer>>> indexMap = new TreeMap<>();
            FileProcessor.traverseDirectory(Paths.get(input), wordCountMap, indexMap);

            String wordCountMap_JSON = JsonWriter.writeObject(wordCountMap);
            String indexMap_JSON = JsonWriter.writeWordPositionsMap(indexMap);

            if (countOutput != null) {
							writeFile(countOutput, wordCountMap_JSON);
						}
            if (indexOutput != null) {
							writeFile(indexOutput, indexMap_JSON);
						}
        } else if (Files.isRegularFile(Path.of(input))) {
            Pair<Integer, TreeMap<String, TreeMap<String, ArrayList<Integer>>>> maps = FileProcessor.readFile(Path.of(input)); // TODO Should only need 1 map here?

            int wordCount = maps.getLeft();
            String wordCountMap = wordCount > 1 ? JsonWriter.writeObject(Map.of(input, wordCount)) :
                    JsonWriter.writeObject(Collections.emptyMap());

            TreeMap<String, TreeMap<String, ArrayList<Integer>>> index = maps.getRight();
            String indexMap = index.size() > 0 ? JsonWriter.writeWordPositionsMap(index) :
                    JsonWriter.writeObject(Collections.emptyMap());

            if (countOutput != null) {
							writeFile(countOutput, wordCountMap);
						}
            if (indexOutput != null) {
							writeFile(indexOutput, indexMap);
						}
        }
        */
    }

	/**
	 * Writes the specified content to the file located at the given file path.
	 *
	 * @param path    the path of the file to write to
	 * @param content the content to write to the file
	 */
	private static void writeFile(String path, String content) {
	    try (Writer writer = new FileWriter(path)) {
	        writer.write(content);
	    } catch (IOException e) {
	        System.err.println("Error: " + e.getMessage());
	    }
	}
}
