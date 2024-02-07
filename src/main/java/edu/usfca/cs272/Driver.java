package edu.usfca.cs272;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

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
		// store initial start time
		Instant start = Instant.now();

		System.out.println("Working Directory: " + Path.of(".").toAbsolutePath().normalize().getFileName());
//		System.out.println("Arguments: " + Arrays.toString(args));
		
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
         
		System.out.println("-text: " + textPath + " -count: " + countsPath);

	}
	
	

	/*
	 * Generally, "Driver" classes are responsible for setting up and calling other
	 * classes, usually from a main() method that parses command-line parameters.
	 * Generalized reusable code are usually placed outside of the Driver class.
	 * They are sometimes called "Main" classes too, since they usually include the
	 * main() method.
	 *
	 * If the driver were only responsible for a single class, we use that class
	 * name. For example, "TaxiDriver" is what we would name a driver class that
	 * just sets up and calls the "Taxi" class.
	 *
	 * The starter code (calculating elapsed time) is not necessary. It can be
	 * removed from the main method.
	 *
	 * TODO Delete this after reading.
	 */
}
