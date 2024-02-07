package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeSet;

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
		
		System.out.println("Working Directory: " + Path.of(".").toAbsolutePath().normalize().getFileName());
		
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
            	} else {
        			System.err.print("Error: Missing argument for -counts");
        			return;
        		}
            	
            }
        }
         
        if (input != null) {
            int wordCount = countWords(input);
            String res = JsonWriter.writeObject(Map.of(input, wordCount));
            outputWordCount(output, res);
        } else {
            System.err.println("Error: No input text file");
        }

	}
	
	private static int countWords(String filePath) {
        int wordCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                wordCount += words.length;
            }
            return wordCount;
        } catch (IOException e) {
            System.err.println("Error reading input text file");
            return -1;
        }
    }
	
    private static void outputWordCount(String filePath, String res) {
        try (Writer writer = new FileWriter(filePath)) {
            writer.write(res);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
	
}
