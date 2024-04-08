package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @author Evana Pradhan
 * @version Spring 2024
 */
public class JsonWriter {
	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {
		writer.write("[");

		var iterator = elements.iterator();

		if (!elements.isEmpty()) {
			writer.write(System.lineSeparator());

			writeIndent(iterator.next().toString(), writer, indent + 1);

			while (iterator.hasNext()) {
				writer.write(",");
				writer.write(System.lineSeparator());
				writeIndent(iterator.next().toString(), writer, indent + 1);
			}
		}
		writer.write(System.lineSeparator());
		writeIndent("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Writer writer, int indent) throws IOException {
		writer.write("{");
		var iterator = elements.entrySet().iterator();

		if (iterator.hasNext()) {
			writer.write(System.lineSeparator());
			var firstEntry = iterator.next();
			writeIndent('"' + firstEntry.getKey() + "\": " + firstEntry.getValue(), writer, indent + 1);

			while (iterator.hasNext()) {
				writer.write(",");
				writer.write(System.lineSeparator());
				var entry = iterator.next();
				writeIndent('"' + entry.getKey() + "\": " + entry.getValue(), writer, indent + 1);
			}
		}
		writer.write(System.lineSeparator());
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("{");

		Iterator<? extends Map.Entry<String, ? extends Collection<? extends Number>>> iterator = elements.entrySet()
				.iterator();

		if (iterator.hasNext()) {
			writer.write(System.lineSeparator());

			Map.Entry<String, ? extends Collection<? extends Number>> firstEntry = iterator.next();
			writeIndent('"' + firstEntry.getKey() + "\": ", writer, indent + 1);
			writeArray(firstEntry.getValue(), writer, indent + 1);

			while (iterator.hasNext()) {
				writer.write(",");
				writer.write(System.lineSeparator());

				Map.Entry<String, ? extends Collection<? extends Number>> entry = iterator.next();
				writeIndent('"' + entry.getKey() + "\": ", writer, indent + 1);
				writeArray(entry.getValue(), writer, indent + 1);
			}
		}

		writer.write(System.lineSeparator());
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectArrays(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("[");
		Iterator<? extends Map<String, ? extends Number>> iterator = elements.iterator();

		if (iterator.hasNext()) {
			writer.write(System.lineSeparator());
			writeIndent("", writer, indent + 1);
			writeObject(iterator.next(), writer, indent + 1);

			while (iterator.hasNext()) {
				writer.write(",");
				writer.write(System.lineSeparator());
				Map<String, ? extends Number> element = iterator.next();
				writeIndent("", writer, indent + 1);
				writeObject(element, writer, indent + 1);
			}
		}

		writer.write(System.lineSeparator());
		writeIndent("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArrayObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArrayObjects(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes a key-value pair representing a collection of numbers to a writer,
	 * with optional indentation.
	 *
	 * @param entry The key-value pair to be written.
	 * @param writer The writer to which the key-value pair will be written.
	 * @param indent The number of spaces to indent the output by.
	 * @throws IOException if an I/O error occurs while writing to the writer.
	 */
	public static void writeObjectCollection(Map.Entry<String, ? extends Collection<? extends Number>> entry,
			Writer writer, int indent) throws IOException {
		writer.write("\"" + entry.getKey() + "\": ");
		writeArray(entry.getValue(), writer, indent);
	}

	/**
	 * Writes the contents of the word positions map to the specified writer in JSON
	 * format with the given indentation level. If the word positions map is empty,
	 * it writes an empty JSON object.
	 *
	 * @param wordPositionsMap the word positions map to write
	 * @param writer the writer to write the JSON content to
	 * @param indent the indentation level for formatting the JSON
	 * @throws IOException if an I/O error occurs while writing the JSON content
	 */
	public static void writeWordPositionsMap(
			Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> wordPositionsMap, Writer writer,
			int indent) throws IOException {

		writer.write("{");

		var iterator = wordPositionsMap.entrySet().iterator();

		if (iterator.hasNext()) {
			writer.write(System.lineSeparator());

			var firstEntry = iterator.next();
			writeIndent('"' + firstEntry.getKey() + "\": ", writer, indent + 1);
			writeObjectArrays(firstEntry.getValue(), writer, indent + 1);

			while (iterator.hasNext()) {
				writer.write(",");
				writer.write(System.lineSeparator());

				var entry = iterator.next();
				writeIndent('"' + entry.getKey() + "\": ", writer, indent + 1);
				writeObjectArrays(entry.getValue(), writer, indent + 1);
			}
		}

		writer.write(System.lineSeparator());
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes the contents of the word positions map to the specified file path in
	 * JSON format. If the word positions map is empty, it writes an empty JSON
	 * object.
	 *
	 * @param wordPositionsMap the word positions map to write
	 * @param path the path to the file to write the JSON content to
	 * @throws IOException if an I/O error occurs while writing the JSON content
	 */
	public static void writeWordPositionsMap(
			Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> wordPositionsMap, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeWordPositionsMap(wordPositionsMap, writer, 0);
		}
	}

	/**
	 * Writes the contents of the word positions map to a JSON string.
	 *
	 * @param wordPositionsMap the word positions map to convert to JSON
	 * @return a JSON string representing the word positions map
	 */
	public static String writeWordPositionsMap(
			Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> wordPositionsMap) {
		try {
			StringWriter writer = new StringWriter();
			writeWordPositionsMap(wordPositionsMap, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes search results to a writer JSON format.
	 *
	 * @param elements The TreeMap containing search results.
	 * @param writer The Writer object to write the results to.
	 * @param indent The number of spaces for indentation.
	 * @throws IOException If an I/O error occurs while writing.
	 */
	public static void writeSearchResults(Map<String, ? extends Collection<IndexSearcher>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("{");

		var iterator = elements.entrySet().iterator();

		if (iterator.hasNext()) {
			writer.write(System.lineSeparator());

			var firstEntry = iterator.next();
			writeIndent('"' + firstEntry.getKey() + "\": ", writer, indent + 1);
			writeSearcherArray(firstEntry.getValue(), writer, indent + 1);

			while (iterator.hasNext()) {
				writer.write(",");
				writer.write(System.lineSeparator());

				var entry = iterator.next();
				writeIndent('"' + entry.getKey() + "\": ", writer, indent + 1);
				writeSearcherArray(entry.getValue(), writer, indent + 1);
			}
		}

		writer.write(System.lineSeparator());
		writeIndent("}", writer, indent);

	}

	/**
	 * Writes the elements of an ArrayList of IndexSearcher objects in JSON format
	 * to the provided Writer. The elements are written as an array, with each
	 * IndexSearcher object represented as a JSON string. An optional indentation
	 * level can be specified to format the JSON output.
	 *
	 * @param elements the ArrayList of IndexSearcher objects to be written
	 * @param writer the Writer object to which the JSON output will be written
	 * @param indent the number of spaces to use for indentation (0 for no
	 *   indentation)
	 * @throws IOException if an I/O error occurs while writing to the Writer
	 */
	public static void writeSearcherArray(Collection<IndexSearcher> elements, Writer writer, int indent)
			throws IOException {
		writer.write("[");
		if (!elements.isEmpty()) {

			var iterator = elements.iterator();
			while (iterator.hasNext()) {
				writer.write(System.lineSeparator());

				IndexSearcher searcher = iterator.next();
				String searcherJson = searcher.toString();
				String indentedSearcherJson = indentJson(searcherJson, indent + 1);
				writer.write(indentedSearcherJson);

				if (iterator.hasNext()) {
					writer.write(",");
				}
			}
		}
		writer.write(System.lineSeparator());
		writeIndent("]", writer, indent);
	}

	/**
	 * Applies indentation to IndexSearcher string.
	 *
	 * @param searcher the IndexSearcher string to be indented.
	 * @param indent the number of spaces to use for indentation.
	 * @return the indented JSON string.
	 */
	private static String indentJson(String searcher, int indent) {
		String indentSpace = "  ".repeat(indent);
		String[] lines = searcher.split("\n");
		return Arrays.stream(lines).map(line -> indentSpace + line).collect(Collectors.joining("\n"));
	}

	/**
	 * Writes the contents of the search results map to the specified file path in
	 * JSON format. If the word positions map is empty, it writes an empty JSON
	 * object.
	 * 
	 * @param elements The TreeMap containing search results.
	 * @param path The Path object representing the file path.
	 * @throws IOException If an I/O error occurs while writing.
	 */

	public static void writeSearchResults(Map<String, ? extends Collection<IndexSearcher>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeSearchResults(elements, writer, 0);
		}
	}

	/**
	 * Writes the contents of the search results map t to a JSON string.
	 *
	 * @param elements The TreeMap containing search results.
	 * @return A string representation of the search results.
	 */
	public static String writeSearchResults(Map<String, ? extends Collection<IndexSearcher>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeSearchResults(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/** Prevent instantiating this class of static methods. */
	private JsonWriter() {
	}
}
