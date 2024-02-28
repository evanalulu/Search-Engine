package edu.usfca.cs272;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
    public static void main(String[] args) {
        ArgumentParser parser = new ArgumentParser(args);
    	InvertedIndex index = new InvertedIndex();	
        
        Path input = null;
        Path countOutput = null;
        Path indexOutput = null;
        
        if (parser.hasFlag("-text")) {
        	if (!parser.hasValue("-text")) {
        		System.out.println("Input path -text not provided");
        		return;
        	} else if (Files.exists(parser.getPath("-text"))) {
        		input = parser.getPath("-text");
        		System.out.println(input);
        	} else {
        		System.out.println("Invalid path");
        		return;
        	}
        }
        
        if (parser.hasFlag("-counts")) {
            countOutput = parser.getPath("-counts", Path.of("counts.json"));
		    if (countOutput != null && !parser.hasFlag("-text")) {
		    	try {
					JsonWriter.writeObject(index.getWordCountMap(), countOutput);
					return;
				} catch (IOException e) {

				}
		    }
        }

    	if (parser.hasFlag("-index")) {
		    indexOutput = parser.getPath("-index", Path.of("index.json"));
		    if (indexOutput != null && !parser.hasFlag("-text")) {
		    	try {
					JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
					return;
				} catch (IOException e) {

				}
		    }
		}
    	
        if (parser.empty()) {
        	System.out.println("No arguments provided");
        	return;
        }
    	    	
    	if (Files.isDirectory(input)) {
    	    try {
				FileProcessor.traverseDirectory(input, index);
			} catch (IOException e) {

			}
    	    if (countOutput != null)
				try {
					JsonWriter.writeObject(index.getWordCountMap(), countOutput);
				} catch (IOException e) {

				}
    	    if (indexOutput != null)
				try {
					JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
				} catch (IOException e) {

				}
    	} else {
    	    try {
				FileProcessor.readFile(input, index);
			} catch (IOException e) {

			}
    	    if (countOutput != null)
				try {
					JsonWriter.writeObject(index.getWordCountMap(), countOutput);
				} catch (IOException e) {

				}
    	    if (indexOutput != null)
				try {
					JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
				} catch (IOException e) {

				}
    	}
    }
}
