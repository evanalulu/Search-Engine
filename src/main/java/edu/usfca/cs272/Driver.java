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
	
		if (parser.hasFlag("-text")) {
			Path input = parser.getPath("-text");
			
			if (input == null) {
				System.out.println("Error: Input path is null. Please provide a valid input path.");
				return;
			}
			
			try {
				if (Files.isDirectory(input)) {
					FileProcessor.traverseDirectory(input, index);
				} else {
					FileProcessor.readFile(input, index);
				}
			} catch (IOException e) {
				System.out.println("Unable to build the inverted index from path: " + input);
			}
		}
		Path countOutput = null;
		Path indexOutput = null;
		
		if (parser.hasFlag("-counts")) {
			countOutput = parser.getPath("-counts", Path.of("counts.json"));
			/** Only -counts with no path passed */
			if (countOutput != null) {
				try {
					System.out.println(index.getWordCountMap().size());
					JsonWriter.writeObject(index.getWordCountMap(), countOutput);
				} catch (IOException e) {
					System.out.println("Error writing word count data: " + e.getMessage());
				}
			}
		}
	
		if (parser.hasFlag("-index")) {
			indexOutput = parser.getPath("-index", Path.of("index.json"));
			/** Only -index with no path passed */
			if (indexOutput != null) {
				try {
					JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
				} catch (IOException e) {
					System.out.println("Error writing index data: " + e.getMessage());
	
				}
			}
		}
	}
}