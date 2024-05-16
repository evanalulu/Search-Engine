package edu.usfca.cs272;

import java.io.IOException;
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

		InvertedIndex index = null;
		ThreadSafeInvertedIndex threadSafeIndex = null;
		WorkQueue queue = null;
		QueryProcessorInterface search = null;

		boolean multithread = parser.hasFlag("-threads");
		boolean isPartial = parser.hasFlag("-partial");

		if (multithread) {
			threadSafeIndex = new ThreadSafeInvertedIndex();
			index = threadSafeIndex;

			int threads = parser.getInteger("-threads", 5);
			if (threads < 1) {
				threads = 5;
			}

			queue = new WorkQueue(threads);

			search = new QueuedQueryProcessor(threadSafeIndex, isPartial, queue);
		}
		else {
			index = new InvertedIndex();
		}

		if (parser.hasFlag("-text")) {
			Path input = parser.getPath("-text");

			if (input == null) {
				System.out.println("Error: Input path is null. Please provide a valid input path.");
				return;
			}

			try {
				if (multithread) {
					QueuedFileProcessor.processPath(input, threadSafeIndex, queue);
				}
				else {
					FileProcessor.processPath(input, index);
				}
			}
			catch (IOException e) {
				System.out.println("Unable to build the inverted index from path: " + input);
			}
		}

		if (parser.hasFlag("-query")) {
			Path query = parser.getPath("-query");
			if (query != null) {
				try {
					if (search != null) {
						search.processQueries(query);
					}
				}
				catch (IOException e) {
					System.err.println("Error getting search results: " + e.getMessage());
				}
			}
		}

		if (queue != null) {
			queue.shutdown();
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

		if (parser.hasFlag("-results")) {
			Path resultsOutput = parser.getPath("-results", Path.of("results.json"));
			try {
				search.writeSearchResults(resultsOutput);
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

		if (queue != null) {
			queue.join();
		}

	}
}
