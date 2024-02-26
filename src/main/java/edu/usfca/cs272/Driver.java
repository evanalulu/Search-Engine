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
    public static void main(String[] args) throws IOException { // TODO Don't throw here
    	/* TODO 
    	ArgumentParser parser = new ArgumentParser(args);
    	InvertedIndex index = new InvertedIndex();
    	
    	if(parser.hasFlag("-text")) {
    		Path path = parser.getPath("-text");
    		
    		try {
    			1 line of code... calling your processor class
    		}
    		catch ( ) {
    			
    		}
    	}
    	
    	if(parser.hasFlag("-counts")) {
    		Path path = parser.getPath("-counts", Path.of("counts.json"));
    		
    		try {
    			
    		}
    		catch {
    			
    		}
    	}
    	
    	*/

        ArgumentParser argsMap = new ArgumentParser(args);

        String input = argsMap.hasFlag("-text") ? argsMap.getString("-text") : null;
        String countOutput = null;
        String indexOutput = null;

        if (argsMap.hasFlag("-counts")) {
					countOutput = argsMap.hasValue("-counts") ? argsMap.getString("-counts") : "counts.json";
				}

        if (argsMap.hasFlag("-index")) {
					indexOutput = argsMap.hasValue("-index") ? argsMap.getString("-index") : "index.json";
				}

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

/*
 * TODO
Description	Resource	Path	Location	Type
Javadoc: Description expected after this reference	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 42	Java Problem
Javadoc: Missing comment for public declaration	ArgumentParser.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 128	Java Problem
Javadoc: Missing comment for public declaration	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 374	Java Problem
Javadoc: Missing comment for public declaration	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 425	Java Problem
Javadoc: Missing comment for public declaration	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 431	Java Problem
The import java.io.BufferedReader is never used	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 6	Java Problem
The import java.io.FileReader is never used	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 7	Java Problem
The import java.nio.charset.StandardCharsets.UTF_8 is never used	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 3	Java Problem
The import java.nio.file.DirectoryStream is never used	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 11	Java Problem
The import java.util.Arrays is never used	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 16	Java Problem
The import java.util.List is never used	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 18	Java Problem
The import java.util.List is never used	FileStemmer.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 15	Java Problem
The import java.util.Map.Entry is never used	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 20	Java Problem
The import java.util.Map.Entry is never used	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 18	Java Problem
The import java.util.Map is never used	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 21	Java Problem
The import java.util.stream.Collectors is never used	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 20	Java Problem
The import java.util.stream.Stream is never used	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 23	Java Problem
The import java.util.TreeSet is never used	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 22	Java Problem
*/
