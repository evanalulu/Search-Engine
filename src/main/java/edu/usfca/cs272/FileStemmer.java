package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Utility class for parsing, cleaning, and stemming text and text files into
 * collections of processed words.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @author Evana Pradhan
 * @version Spring 2024
 */
public class FileStemmer {
	/** Regular expression that matches any whitespace. **/
	public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");

	/** Regular expression that matches non-alphabetic characters. **/
	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");

	/**
	 * Cleans the text by removing any non-alphabetic characters (e.g. non-letters
	 * like digits, punctuation, symbols, and diacritical marks like the umlaut) and
	 * converting the remaining characters to lowercase.
	 *
	 * @param text the text to clean
	 * @return cleaned text
	 */
	public static String clean(String text) {
		String cleaned = Normalizer.normalize(text, Normalizer.Form.NFD);
		cleaned = CLEAN_REGEX.matcher(cleaned).replaceAll("");
		return cleaned.toLowerCase();
	}

	/**
	 * Splits the supplied text by whitespaces.
	 *
	 * @param text the text to split
	 * @return an array of {@link String} objects
	 */
	public static String[] split(String text) {
		return text.isBlank() ? new String[0] : SPLIT_REGEX.split(text.strip());
	}

	/**
	 * Parses the text into an array of clean words.
	 *
	 * @param text the text to clean and split
	 * @return an array of {@link String} objects
	 *
	 * @see #clean(String)
	 * @see #parse(String)
	 */
	public static String[] parse(String text) {
		return split(clean(text));
	}

	/**
	 * Parses the line into cleaned and stemmed words and adds them to the provided
	 * collection.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param stems the collection to add stems
	 *
	 * @see #parse(String)
	 * @see Stemmer#stem(CharSequence)
	 * @see Collection#add(Object)
	 */
	public static void addStems(String line, Stemmer stemmer, Collection<String> stems) {
	    String clean = clean(line);
	    String[] split = split(clean);

	    for (String element : split) {
	        String[] parsed = parse(element);
	        
	        /* CITE: Derived from ChatGPT prompt 
	         * "How to use stemmer.stem(string) when string is stemmer.stem(charSequence).toString()?
	         * Simplified version of code:
	         * String[] parsed = parse(element);
	         * CharSequence charSequence = Arrays.toString(parsed);
	         * String stemmedString = stemmer.stem(charSequence).toString();
	         * I think that the Arrays.toString(parsed) part might be causing the problem. How can I 
	         * properly concatenate the individual words from the parsed array into a single string 
	         * before applying stemming?" */
	        
	        StringBuilder concatenated = new StringBuilder();
	        for (String word : parsed) {
	            concatenated.append(word);
	        }

	        stems.add(stemmer.stem(concatenated.toString()).toString());
	    }
	}

	

	/**
	 * Parses the line into a list of cleaned and stemmed words.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see #parse(String)
	 * @see Stemmer#stem(CharSequence)
	 * @see #addStems(String, Stemmer, Collection)
	 */
	public static ArrayList<String> listStems(String line, Stemmer stemmer) {
		ArrayList<String> stems = new ArrayList<>();
		addStems(line, stemmer, stems);
		return stems;
	}

	/**
	 * Parses the line into a list of cleaned and stemmed words using the default
	 * stemmer for English.
	 *
	 * @param line the line of words to parse and stem
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see SnowballStemmer#SnowballStemmer(ALGORITHM)
	 * @see ALGORITHM#ENGLISH
	 * @see #listStems(String, Stemmer)
	 */
	public static ArrayList<String> listStems(String line) {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		ArrayList<String> stems = listStems(line, stemmer);
		return stems;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words
	 * using the default stemmer for English.
	 *
	 * @param input the input file to parse and stem
	 * @return a list of stems from file in parsed order
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #listStems(String, Stemmer)
	 */
	public static ArrayList<String> listStems(Path input) throws IOException {
		ArrayList<String> stems = new ArrayList<>();
		
		try (
				BufferedReader reader = Files.newBufferedReader(input, UTF_8);
		) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				ArrayList<String> temp = listStems(line);
				for (String element: temp) 
					stems.add(element);
			}
		}
		
		return stems;
	}

	/**
	 * Parses the line into a set of unique, sorted, cleaned, and stemmed words.
	 *
	 * @param line the line of words to parse and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see #parse(String)
	 * @see Stemmer#stem(CharSequence)
	 * @see #addStems(String, Stemmer, Collection)
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		ArrayList<String> stems = listStems(line, stemmer);
		TreeSet<String> uniques = new TreeSet<>();
		uniques.addAll(stems);
		return uniques;
		
	}

	/**
	 * Parses the line into a set of unique, sorted, cleaned, and stemmed words
	 * using the default stemmer for English.
	 *
	 * @param line the line of words to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer#SnowballStemmer(ALGORITHM)
	 * @see ALGORITHM#ENGLISH
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(String line) {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		return uniqueStems(line, stemmer);
	}

	/**
	 * Reads a file line by line, parses each line into a set of unique, sorted,
	 * cleaned, and stemmed words using the default stemmer for English.
	 *
	 * @param input the input file to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(Path input) throws IOException {
		ArrayList<String> stems = listStems(input);
		TreeSet<String> uniques = new TreeSet<>();
		uniques.addAll(stems);
		return uniques;
	}

	/**
	 * Reads a file line by line, parses each line into unique, sorted, cleaned, and
	 * stemmed words using the default stemmer for English, and adds the set of
	 * unique sorted stems to a list per line in the file.
	 *
	 * @param input the input file to parse and stem
	 * @return a list where each item is the sets of unique sorted stems parsed from
	 *   a single line of the input file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static ArrayList<TreeSet<String>> listUniqueStems(Path input) throws IOException {
		ArrayList<TreeSet<String>> uniqueStems = new ArrayList<>();
		
		try (
				BufferedReader reader = Files.newBufferedReader(input, UTF_8);
		) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				TreeSet<String> uniques = uniqueStems(line);
				uniqueStems.add(uniques);
			}
		}
		
		
		return uniqueStems;
	}

	/**
	 * Demonstrates this class.
	 *
	 * @param args unused
	 * @throws IOException if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		// demonstrates how to use split, clean, and parse
		System.out.println("____PARSING DEMO____");
		System.out.println();

		String sally = """
				Sally Sue...\t sells 76 sea-shells
				at THE sEa_shorE soirée!""";

		System.out.println("Original:");
		System.out.println(sally);
		System.out.println();

		System.out.println("Cleaned:");
		System.out.println(clean(sally));
		System.out.println();

		System.out.println(" Split: " + Arrays.toString(split(sally)));
		System.out.println("Parsed: " + Arrays.toString(parse(sally)));
		System.out.println();

		// demonstrates how to use stemmer
		System.out.println("____STEMMER DEMO____");
		System.out.println();

		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		String demo = "practicing";
		String stem = stemmer.stem(demo).toString();

		System.out.println("Word: " + demo);
		System.out.println("Stem: " + stem);
		System.out.println();

		// demonstrates how to use list/uniqueStems methods
		System.out.println("____STEMMING TEXT____");
		System.out.println();

		String practice = """
				practic practical practice practiced practicer practices
				practicing practis practisants practise practised practiser
				practisers practises practising practitioner practitioners
				""";

		System.out.println("Original: \n" + practice);
		System.out.println("  List: " + listStems(practice));
		System.out.println("Unique: " + uniqueStems(practice));
		System.out.println();
		
		//////////////////////////////////////////////

		
		System.out.println("____STEMMING TEXT____");
		System.out.println();

		String practice2 = """
				conspicuously
				""";

		System.out.println("Original: \n" + practice2);
		System.out.println("  List: " + listStems(practice2));
		System.out.println("Unique: " + uniqueStems(practice2));
		System.out.println();
		
//		//////////////////////////////////////////////
//
		// demonstrates stemming files
		System.out.println("____STEMMING FILE____");
		System.out.println();

		Path base = Path.of("src", "test", "resources", "stemmer");
		Path file = base.resolve("cleaner.txt");
		String input = Files.readString(file, UTF_8);

		System.out.println("Original:\n" + input);

		System.out.println("       List: " + listStems(file));
		System.out.println("     Unique: " + uniqueStems(file));
		System.out.println("List Unique: " + listUniqueStems(file));
	}

	/** Prevent instantiating this class of static methods. */
	private FileStemmer() {
	}
}
