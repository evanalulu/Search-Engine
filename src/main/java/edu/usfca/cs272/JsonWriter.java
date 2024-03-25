package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
			writer.write("\n");

			writeIndent(iterator.next().toString(), writer, indent + 1);

			while (iterator.hasNext()) {
				writer.write(",\n");
				writeIndent(iterator.next().toString(), writer, indent + 1);
			}
		}
		writer.write("\n");
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

		Iterator<? extends Map.Entry<String, ? extends Number>> iterator = elements.entrySet().iterator();

		if (!elements.isEmpty()) {
			writer.write("\n");
			if (iterator.hasNext()) {
				Map.Entry<String, ? extends Number> firstEntry = iterator.next();
				writeIndent('"' + firstEntry.getKey() + "\": " + firstEntry.getValue(), writer, indent + 1);
			}

			while (iterator.hasNext()) {
				Map.Entry<String, ? extends Number> entry = iterator.next();
				String key = entry.getKey();
				Number value = entry.getValue();

				writer.write(",\n");
				writeIndent('"' + key + "\": " + value.toString(), writer, indent + 1);
			}
		}
		writer.write("\n");
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
		if (!elements.isEmpty()) {
			writer.write(System.lineSeparator());
			Iterator<? extends Map.Entry<String, ? extends Collection<? extends Number>>> iterator = elements.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, ? extends Collection<? extends Number>> entry = iterator.next();
				String key = entry.getKey();
				Collection<? extends Number> values = entry.getValue();

				writeIndent('"' + key + "\": ", writer, indent + 1);
				writeArray(values, writer, indent + 1);

				if (iterator.hasNext()) {
					writer.write(",");
					writer.write(System.lineSeparator());
				}
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
		if (!elements.isEmpty()) {
			writer.write(System.lineSeparator());
			Iterator<? extends Map<String, ? extends Number>> iterator = elements.iterator();
			while (iterator.hasNext()) {
				Map<String, ? extends Number> element = iterator.next();
				writeIndent(writer, indent + 1);
				writeObject(element, writer, indent + 1);

				if (iterator.hasNext()) {
					writer.write(",");
					writer.write(System.lineSeparator());
				}
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
	 * Writes the contents of the word positions map to the specified writer in JSON
	 * format with the given indentation level. If the word positions map is empty,
	 * it writes an empty JSON object.
	 *
	 * @param wordPositionsMap the word positions map to write
	 * @param writer the writer to write the JSON content to
	 * @param indent the indentation level for formatting the JSON
	 * @throws IOException if an I/O error occurs while writing the JSON content
	 */
	public static void writeWordPositionsMap(TreeMap<String, TreeMap<String, ArrayList<Integer>>> wordPositionsMap,
			Writer writer, int indent) throws IOException {
		/*
		 * TODO Try to make this type more generic (here and in other methods in this
		 * class) so that it works with any type of map and collection and number. Use
		 * the other methods as a clue of how to make this work. The ? extends syntax is
		 * important for nested types! Reach out on Piazza if you run into issues---it
		 * is a really hard generic type to get just right!
		 */

		/*
		 * TODO So much duplicate code! What JsoNWriter method could you reuse here for
		 * the inner treemap?
		 */
		if (wordPositionsMap.isEmpty()) {
			writer.write("{");
			writer.write(System.lineSeparator());
			writer.write("}");
		}
		else {
			writer.write("{");
			if (!wordPositionsMap.isEmpty()) {
				writer.write(System.lineSeparator());
				int counter = 0;
				for (Map.Entry<String, TreeMap<String, ArrayList<Integer>>> entry : wordPositionsMap.entrySet()) {
					writeIndent(writer, indent + 1);
					writer.write("\"" + entry.getKey() + "\": ");
					writer.write("{");
					if (!entry.getValue().isEmpty()) {
						writer.write(System.lineSeparator());
						int innerCounter = 0;
						for (Map.Entry<String, ArrayList<Integer>> innerEntry : entry.getValue().entrySet()) {
							writeIndent(writer, indent + 2);
							writer.write("\"" + innerEntry.getKey() + "\": ");
							ArrayList<Integer> indices = innerEntry.getValue();
							writer.write("[");
							if (!indices.isEmpty()) {
								writer.write(System.lineSeparator());
								Iterator<? extends Number> iterator = indices.iterator();
								while (iterator.hasNext()) {
									Number element = iterator.next();
									writeIndent(element.toString(), writer, indent + 3);
									if (iterator.hasNext()) {
										writer.write(",");
										writer.write(System.lineSeparator());
									}
								}
							}

							writer.write(System.lineSeparator());
							writeIndent("]", writer, indent + 2);
							if (++innerCounter < entry.getValue().size()) {
								writer.write(",");
								writer.write(System.lineSeparator());
							}
						}
						writer.write(System.lineSeparator());
					}
					writeIndent(writer, indent + 1);
					writer.write("}");
					if (++counter < wordPositionsMap.size()) {
						writer.write(",");
						writer.write(System.lineSeparator());
					}
				}
				writer.write(System.lineSeparator());
			}
			writeIndent("}", writer, indent);
		}
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
	public static void writeWordPositionsMap(TreeMap<String, TreeMap<String, ArrayList<Integer>>> wordPositionsMap,
			Path path) throws IOException {
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
	public static String writeWordPositionsMap(TreeMap<String, TreeMap<String, ArrayList<Integer>>> wordPositionsMap) {
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
	 * Demonstrates this class.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) { // TODO Remove
		Set<Integer> empty = Collections.emptySet();
		Set<Integer> single = Set.of(42);
		List<Integer> simple = List.of(65, 66, 67);

		System.out.println("\nArrays:");
		System.out.println(writeArray(empty));
		System.out.println(writeArray(single));
		System.out.println(writeArray(simple));

		System.out.println("\nObjects:");
		System.out.println(writeObject(Collections.emptyMap()));
		System.out.println(writeObject(Map.of("hello", 42)));
		System.out.println(writeObject(Map.of("hello", 42, "world", 67)));

		System.out.println("\nNested Arrays:");
		System.out.println(writeObjectArrays(Collections.emptyMap()));
		System.out.println(writeObjectArrays(Map.of("hello", single)));
		System.out.println(writeObjectArrays(Map.of("hello", single, "world", simple)));

		System.out.println("\nNested Objects:");
		System.out.println(writeArrayObjects(Collections.emptyList()));
		System.out.println(writeArrayObjects(Set.of(Map.of("hello", 3.12))));
		System.out.println(writeArrayObjects(Set.of(Map.of("hello", 3.12, "world", 2.04), Map.of("apple", 0.04))));
	}

	/** Prevent instantiating this class of static methods. */
	private JsonWriter() {
	}
}
