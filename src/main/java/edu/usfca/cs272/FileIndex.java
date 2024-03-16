package edu.usfca.cs272;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A special type of {@link ForwardIndex} that indexes the UNIQUE words that
 * were found in a text file (represented by {@link Path} objects).
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @author Evana Pradhan
 * @version Spring 2024
 */
public class FileIndex implements ForwardIndex<Path> {

	/**
	 * The index that maps file paths to sets of unique words found in each file.
	 */
    private final Map<Path, Set<String>> index;
    
	/**
	 * Retrieves the index mapping file paths to sets of unique words found in each file.
	 *
	 * @return the index map
	 */
	public Map<Path, Set<String>> getIndex() {
		return index;
	}

	/**
	 * Constructs a new FileIndex.
	 */
    public FileIndex() {
        this.index = new HashMap<>();
    }

	/**
	 * Adds a word to the index for the specified location.
	 *
	 * @param location the location (file path) where the word was found
	 * @param word the word to add to the index
	 */
    @Override
	public void add(Path location, String word) {
        getIndex().computeIfAbsent(location, k -> new HashSet<>()).add(word);
    }

	/**
	 * Returns the total number of indexed locations.
	 *
	 * @return the size of the index
	 */
    @Override
	public int size() {
        return getIndex().size();
    }  
    
	/**
	 * Returns the number of unique words indexed for the specified location.
	 *
	 * @param location the location (file path) to query
	 * @return the number of unique words indexed for the specified location
	 */
	@Override
	public int size(Path location) {
        return getIndex().getOrDefault(location, Collections.emptySet()).size();
	}
    
	/**
	 * Checks if the index contains entries for the specified location.
	 *
	 * @param location the location (file path) to check
	 * @return true if the index contains entries for the specified location, otherwise false
	 */
    @Override
	public boolean has(Path location) {
    	return getIndex().containsKey(location);
    }
    
	/**
	 * Checks if the specified word is indexed for the specified location.
	 *
	 * @param location the location (file path) to check
	 * @param word the word to check
	 * @return true if the specified word is indexed for the specified location, otherwise false
	 */
	@Override
	public boolean has(Path location, String word) {
		Set<String> words = getIndex().get(location);
		return words != null && words.contains(word);
	}

	/**
	 * Returns an unmodifiable view of the indexed locations.
	 *
	 * @return an unmodifiable collection of indexed locations
	 */
    @Override
	public Collection<Path> view() {
    	return Collections.unmodifiableCollection(getIndex().keySet());
	}

	/**
	 * Returns an unmodifiable view of the unique words indexed for the specified location.
	 *
	 * @param location the location (file path) to query
	 * @return an unmodifiable set of unique words indexed for the specified location
	 */
    @Override
	public Collection<String> view(Path location) {
		return Collections.unmodifiableSet(getIndex().getOrDefault(location, new HashSet<>()));
	}

	/**
	 * Returns a string representation of the FileIndex.
	 *
	 * @return a string representation of the FileIndex
	 */
    @Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("FileIndex {");
		for (Map.Entry<Path, Set<String>> entry : getIndex().entrySet()) {
		    str.append("\n  ").append(entry.getKey()).append(": ").append(entry.getValue());
		}
		str.append("\n}");
		return str.toString();
	}

	/**
	 * Demonstrates this class.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		ForwardIndex<Path> index = new FileIndex();

		Path hello = Path.of("hello.txt");
		Path world = Path.of("world.txt");

		index.add(hello, List.of("hello", "hola", "aloha", "ciao"));
		index.add(world, List.of("earth", "mars", "venus", "pluto", "mars"));

		System.out.println(index.view());
		System.out.println(index.view(hello));
		System.out.println(index.view(world));
	}
}

