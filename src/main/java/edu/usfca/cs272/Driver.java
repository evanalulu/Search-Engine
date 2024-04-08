package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

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
				FileProcessor.processPath(input, index);
			}
			catch (IOException e) {
				System.out.println("Unable to build the inverted index from path: " + input);
			}
		}

		if (parser.hasFlag("-counts")) {
			Path countOutput = parser.getPath("-counts", Path.of("counts.json"));

			try {
				index.writeWordCountMap(countOutput);
			}
			catch (IOException e) {
				System.out.println("Error writing word count data: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-index")) {
			Path indexOutput = parser.getPath("-index", Path.of("index.json"));

			try {
				index.writeIndexMap(indexOutput);
			}
			catch (IOException e) {
				System.out.println("Error writing index data: " + e.getMessage());
			}
		}

		/*
		 * TODO Move this data structure to a QueryProcessor class as a member
		 * 
		 * Take an instance-based approach
		 * 

	public void getQuery(Path path, ...) throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				getQuery(line, ...)
			}
		}
	}

	public void getQuery(String line, ...) throws IOException {
		Stem the line
		Create the joined string
		and ask the index for the search results
		store the results
	}

	Think about what other useful methods might look like in this class
		 */
		TreeMap<String, ArrayList<IndexSearcher>> result = new TreeMap<>();

		if (parser.hasFlag("-query")) {
			Path query = parser.getPath("-query");
			if (query != null) {
				boolean isPartialSearch = parser.hasFlag("-partial");
				try {
					result = FileProcessor.readQuery(query, index, isPartialSearch);
				}
				catch (IOException e) {
					System.err.println("Error getting search results: " + e.getMessage());
				}
			}
		}

		if (parser.hasFlag("-results")) {
			Path resultsOutput = parser.getPath("-results", Path.of("results.json"));
			try {
				JsonWriter.writeSearchResults(result, resultsOutput);
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

	}
}
