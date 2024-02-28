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
 * @version Spring 2024
 */
public class FileIndex implements ForwardIndex<Path> {
    private final Map<Path, Set<String>> index;

    public FileIndex() {
        this.index = new HashMap<>();
    }

    public void add(Path location, String word) {
        index.computeIfAbsent(location, k -> new HashSet<>()).add(word);
    }

    public int size() {
        return index.size();
    }  
    
	public int size(Path location) {
        return index.getOrDefault(location, Collections.emptySet()).size();
	}
    
    public boolean has(Path location) {
    	return index.containsKey(location);
    }
    
	public boolean has(Path location, String word) {
        Set<String> words = index.get(location);
        return words != null && words.contains(word);
	}

    public Collection<Path> view() {
        return Collections.unmodifiableCollection(index.keySet());
    }

    public Collection<String> view(Path location) {
        return Collections.unmodifiableSet(index.getOrDefault(location, new HashSet<>()));
    }  
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("FileIndex {");
        for (Map.Entry<Path, Set<String>> entry : index.entrySet()) {
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

