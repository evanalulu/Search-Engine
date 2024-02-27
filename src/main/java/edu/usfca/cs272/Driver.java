package edu.usfca.cs272;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

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
        
        if (parser.hasFlag("-counts")) {
            countOutput = parser.getPath("-counts", Path.of("counts.json"));
        }

    	if (parser.hasFlag("-index")) {
		    indexOutput = parser.getPath("-index", Path.of("index.json"));
		}

    	InvertedIndex index = new InvertedIndex();	
    	
    	if (Files.isDirectory(input)) {
    	    FileProcessor.traverseDirectory(input, index);
    	    if (countOutput != null) JsonWriter.writeObject(index.getWordCountMap(), countOutput);
    	    if (indexOutput != null) JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
    	} else {
    	    FileProcessor.readFile(input, index);
    	    if (countOutput != null) JsonWriter.writeObject(index.getWordCountMap(), countOutput);
    	    System.out.println(index.getIndexMap());
    	    if (indexOutput != null) JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
    	}
    }
}
