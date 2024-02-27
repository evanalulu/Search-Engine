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
	 * @throws IOException 
	 */
    public static void main(String[] args) throws IOException {
        ArgumentParser parser = new ArgumentParser(args);
        
        Path input = null;
        Path countOutput = null;
        Path indexOutput = null;
        
        if (parser.hasFlag("-text")) {
		    input = parser.getPath("-text");
		}
        
//        if (parser.hasFlag("-counts")) {
//            countOutput = parser.getPath("-counts", Path.of("counts.json"));
//        }

    	if (parser.hasFlag("-index")) {
		    indexOutput = parser.getPath("-index", Path.of("index.json"));
		}

    	InvertedIndex index = new InvertedIndex();	
    	
    	if (Files.isDirectory(input)) {
            FileProcessor.traverseDirectory(input, index);

//            String wordCountMap_JSON = JsonWriter.writeObject(wordCountMap);
            String indexMap_JSON = JsonWriter.writeWordPositionsMap(index.getIndexMap());

//            if (countOutput != null) {
//							writeFile(countOutput, wordCountMap_JSON);
//						}
            if (indexOutput != null) {
							writeFile(indexOutput, indexMap_JSON);
						}
        } else {
        	FileProcessor.readFile(input, index);
//        	String wordCountMap_JSON = JsonWriter.writeObject(index.getWordCountMap());
//        	writeFile(countOutput, wordCountMap_JSON);
            String indexMap_JSON = JsonWriter.writeWordPositionsMap(index.getIndexMap());
        	writeFile(indexOutput, indexMap_JSON);
        }
    }

	/**
	 * Writes the specified content to the file located at the given file path.
	 *
	 * @param path    the path of the file to write to
	 * @param content the content to write to the file
	 */
	private static void writeFile(Path path, String content) {
	    try (Writer writer = new FileWriter(path.toString())) {
	        writer.write(content);
	    } catch (IOException e) {
	        System.err.println("Error: " + e.getMessage());
	    }
	}
}
