package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
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
        ArgumentParser parser = new ArgumentParser(args);
    	InvertedIndex index = new InvertedIndex();	
    	IndexSearcher search = new IndexSearcher(0, null, null);
    	
    	/** No arguments passed */
        if (parser.empty()) {
        	System.err.println("No arguments provided");
        	return;
        }
        
        Path input = null;
        Path countOutput = null;
        Path indexOutput = null;
        Path query = null;
        ArrayList<IndexSearcher> res = new ArrayList<>();
        
        TreeMap<String, ArrayList<IndexSearcher>> result = new TreeMap<>();
        
        if (parser.hasFlag("-text")) {
        	if (!parser.hasValue("-text")) {
        		System.err.println("Input path -text not provided");
        		return;
        	}
        	
        	if (Files.exists(parser.getPath("-text"))) {
        		input = parser.getPath("-text");
        	} else {
        		System.err.println("Invalid -text path");
        		return;
        	}
        }
        
        if (parser.hasFlag("-counts")) {
            countOutput = parser.getPath("-counts", Path.of("counts.json"));
		    /** Only -counts with no path passed */
		    if (countOutput != null && !parser.hasFlag("-text")) {
		    	try {
					JsonWriter.writeObject(index.getWordCountMap(), countOutput);
					return;
				} catch (IOException e) {
					System.out.println(e.toString());
				}
		    }
        }

    	if (parser.hasFlag("-index")) {
		    indexOutput = parser.getPath("-index", Path.of("index.json"));
		    /** Only -index with no path passed */
		    if (indexOutput != null && !parser.hasFlag("-text")) {
		    	try {
					JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
					return;
				} catch (IOException e) {
					System.out.println(e.toString());
				}
		    }
		}
    	    	
    	if (Files.isDirectory(input)) {
    	    try {
				FileProcessor.traverseDirectory(input, index);
			} catch (IOException e) {

			}
    	} else {
    	    try {
				FileProcessor.readFile(input, index);
			} catch (IOException e) {
				System.out.println(e.toString());
			}
    	} 
    	
    	if (parser.hasFlag("-query")) {
		    query = parser.getPath("-query");
		    Path exactSearch = parser.getPath("-results");
		    try {
		    	result = FileProcessor.readQuery(query, index);
		    	JsonWriter.writeExactSearch(result, exactSearch);
//		    	printFile(exactSearch);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

    	
	    if (countOutput != null)
			try {
				JsonWriter.writeObject(index.getWordCountMap(), countOutput);
			} catch (IOException e) {
				System.out.println(e.toString());
			}
	    if (indexOutput != null)
			try {
				JsonWriter.writeWordPositionsMap(index.getIndexMap(), indexOutput);
			} catch (IOException e) {
				System.out.println(e.toString());
			}
	    
	    

	    String text = new String("pine");
	    testMutability(text);
	    System.out.println(text);
    }
    
    public static void testMutability(String text) {
        text.concat("apple");
    }

    
    public static void printTreeMap(TreeMap<String, ArrayList<IndexSearcher>> result) {
        System.out.println("{");
        for (var entry : result.entrySet()) {
            System.out.println("  \"" + entry.getKey() + "\": [");
            ArrayList<IndexSearcher> searchers = entry.getValue();
            for (int i = 0; i < searchers.size(); i++) {
                IndexSearcher searcher = searchers.get(i);
                System.out.println("    {");
                System.out.println("      \"count\": " + searcher.getCount() + ",");
                System.out.println("      \"score\": " + searcher.getScore() + ",");
                System.out.println("      \"where\": \"" + searcher.getWhere() + "\"");
                if (i == searchers.size() - 1) {
                    System.out.println("    }");
                } else {
                    System.out.println("    },");
                }
            }
            System.out.println("  ],");
        }
        System.out.println("}");
    }
    
    private static void printFile(Path filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toString()))) {
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