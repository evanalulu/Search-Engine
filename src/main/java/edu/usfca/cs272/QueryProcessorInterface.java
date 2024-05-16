package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface QueryProcessorInterface {

	public default void processQueries(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				processQueries(line);
			}
		}
	}

	public abstract void processQueries(String line);
}
