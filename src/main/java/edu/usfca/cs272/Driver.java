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

/*
 * TODO Fix the Javadoc warnings in the code.
 * 
 * Other developers will *not* use poorly unprofessionally documented code
 * regardless of whether the code itself is well designed! It is a tedious but
 * critical step to the final steps of refactoring. The "Configuring Eclipse"
 * guide on the course website shows how to setup Eclipse to see the Javadoc
 * warnings. (Open the "View Screenshot" section.)
 * 
 * As announced on Piazza, when conducting asynchronous reviews, I will no
 * longer review code with warnings or major formatting issues in it. That is a
 * sign you still need to do a cleanup pass of your code. Please do a complete
 * pass of your code for these issues before requesting code review. See the
 * "Project Review" guide for details.
 * 
 * For reference, direct links to the guides and the warnings found are included
 * below.
 * 
 * Because this is only ONE warning, I'll move forward with the review this time.
 * I will not do that again in the future!
 */

// Configuring Eclipse: https://usf-cs272-spring2024.notion.site/Configuring-Eclipse-4f735d746e004dbdbc34af6ad2d988cd#1a1a870909bb45f2a92ef5fc51038635
// Project Review: https://usf-cs272-spring2024.notion.site/Project-Review-c04d5128395a4eb499e30f6fbd0c0352

/*-
Description	Resource	Path	Location	Type
Javadoc: Missing comment for public declaration	ArgumentParser.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 128	Java Problem
*/
