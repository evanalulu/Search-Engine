package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Stream;

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
		
//		System.out.println("Working Directory: " + Path.of(".").toAbsolutePath().normalize().getFileName());
				
		String input = null;
        String output = "counts.json";
        
        for (int i = 0; i < args.length; i ++) {
        	if (args[i].equals("-text")) {
        		if (args.length > i + 1) {
        			input = args[i + 1];
        			i++;
        		} else {
        			System.err.print("Error: Missing argument for -text");
        			return;
        		}
            } else if (args[i].equals("-counts")) {
            	if (args.length > i + 1) {
            		output = args[i + 1];
            	}
            }
        }
        

         
        if (input != null) {
        	if(Files.isDirectory(Paths.get(input))) {
        		Map<String, Integer> wordCountMap = new HashMap<>();
        		try {
                    Stream<Path> paths = Files.list(Paths.get(input));
                    paths.forEach(path -> {
                        if (Files.isRegularFile(path)) {
                            wordCountMap.put(path.toString(), countWords(path.toString()));
                        }
                    });
                    paths.close(); 
                    String res = JsonWriter.writeObject(wordCountMap);
                    outputWordCount(output,res);
                } catch (IOException e) {
                    System.err.println("Error listing files: " + e.getMessage());
                }
        	} else {
	            int wordCount = countWords(input);
	            if (wordCount > 0) {
	            	String res = JsonWriter.writeObject(Map.of(input, wordCount));
	            	outputWordCount(output, res);
	            } else {
	            	String res = JsonWriter.writeObject(Collections.emptyMap());
	            	outputWordCount(output, res);
	            }
        	}
        } else {
            System.err.println("Error: No input text file");
        }
        
        System.out.println();
        printFile(input);
        System.out.println();
        printFile(output);

	}

	
	private static int countWords(String filePath) {
		int wordCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	String clean = FileStemmer.clean(line);
                String[] split = FileStemmer.split(clean);
                wordCount += split.length;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return wordCount;
    }
	
	private static void handleDirectory(String filePath) {
		
	}
	
    private static void outputWordCount(String filePath, String res) {
        try (Writer writer = new FileWriter(filePath)) {
            writer.write(res);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private static void printFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            System.out.println(filePath);
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
	
}
