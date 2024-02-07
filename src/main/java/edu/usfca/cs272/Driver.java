package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
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
		
		String textPath = null;
        String countsPath = "counts.json";
        
        for (int i = 0; i < args.length; i ++) {
        	if (args[i].equals("-text")) {
        		if (args.length > i + 1) {
        			textPath = args[i + 1];
        			i++;
        		} else {
        			System.err.print("Error: Missing argument for -text");
        			return;
        		}
            } else if (args[i].equals("-counts")) {
            	if (args.length > i + 1) {
            		countsPath = args[i + 1];
            	} else {
        			System.err.print("Error: Missing argument for -counts");
        			return;
        		}
            	
            }
        }
         
        if (textPath != null) {
            int wordCount = countWords(textPath);
            System.out.println("Word count: " + wordCount);
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
        } catch (IOException e) {
            System.err.println("Error reading input text file");
        }
        return wordCount;
    }
	
	/*
	try (
			BufferedReader reader = Files.newBufferedReader(input, UTF_8);
	) {
		String line = null;
		while ((line = reader.readLine()) != null) {
			TreeSet<String> uniques = uniqueStems(line);
			uniqueStems.add(uniques);
		}
	}
	*/
	
}
