package edu.usfca.cs272;

import java.io.IOException;
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
	 */
    public static void main(String[] args) {
        ArgumentParser parser = new ArgumentParser(args);
    	InvertedIndex index = new InvertedIndex();	
    	
    	/** No arguments passed */
        if (parser.empty()) {
        	System.err.println("No arguments provided");
        	return;
        }
        
        Path input = null;
        Path countOutput = null;
        Path indexOutput = null;
        
        if (parser.hasFlag("-text")) {
        	if (!parser.hasValue("-text")) {
        		System.err.println("Input path -text not provided");
        		return;
        	}
        	
        	if (Files.exists(parser.getPath("-text"))) {
        		input = parser.getPath("-text");
        	} else {
        		System.err.println("Invalid -text path");
        		return;
        	}
        }
        
        if (parser.hasFlag("-counts")) {
            countOutput = parser.getPath("-counts", Path.of("counts.json"));
		    /** Only -counts with no path passed */
		    if (countOutput != null && !parser.hasFlag("-text")) {
		    	try {
					JsonWriter.writeObject(index.getWordCountMap(), countOutput);
					return;
				} catch (IOException e) {
					System.out.println(e.toString());
				}
		    }
        }

    	if (parser.hasFlag("-index")) {
		    indexOutput = parser.getPath("-index", Path.of("index.json"));
		    /** Only -index with no path passed */
		    if (indexOutput != null && !parser.hasFlag("-text")) {
		    	try {
					JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
					return;
				} catch (IOException e) {
					System.out.println(e.toString());

				}
		    }
		}
    	    	
    	if (Files.isDirectory(input)) {
    	    try {
				FileProcessor.traverseDirectory(input, index);
			} catch (IOException e) {

			}
    	} else {
    	    try {
				FileProcessor.readFile(input, index);
			} catch (IOException e) {
				System.out.println(e.toString());
			}
    	} 
    	
	    if (countOutput != null)
			try {
				JsonWriter.writeObject(index.getWordCountMap(), countOutput);
			} catch (IOException e) {
				System.out.println(e.toString());
			}
	    if (indexOutput != null)
			try {
				JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
			} catch (IOException e) {
				System.out.println(e.toString());
			}
    }
}
