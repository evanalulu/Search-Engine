package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * A thread-safe version of {@link InvertedIndex} using a read/write lock. *
 * 
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	/** The lock used to protect concurrent access to the underlying set. */
	private final MultiReaderLock lock;

	/**
	 * Initializes a thread-safe inverted index.
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new MultiReaderLock();
	}
	
	/*
	 * TODO Rethink which lock to call for your methods
	 */

	@Override
	public void addWord(String word, String location, Integer position) {
		lock.readLock().lock();

		try {
			super.addWord(word, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex other) {
		lock.writeLock().lock();

		try {
			super.addAll(other);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public int getWordCount(String path) {
		lock.readLock().lock();

		try {
			return super.getWordCount(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int getFileCount() {
		lock.readLock().lock();

		try {
			return super.getFileCount();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasFileinCount(String path) {
		// TODO Missing lock
		try {
			return super.hasFileinCount(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasLocation(String word, String location) {
		lock.readLock().lock();
		try {
			return super.hasLocation(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasPosition(String word, String location, Integer position) {
		lock.readLock().lock();
		try {
			return super.hasPosition(word, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numWords(String word) {
		lock.readLock().lock();

		try {
			return super.numWords(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numLocations(String word, String location) {
		lock.readLock().lock();

		try {
			return super.numLocations(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numPositions(String word, String location, Integer position) {
		lock.readLock().lock();

		try {
			return super.numPositions(word, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> viewFiles() {
		lock.readLock().lock();

		try {
			return super.viewFiles();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> viewWords() {
		lock.readLock().lock();

		try {
			return super.viewWords();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> viewLocations(String word) {
		lock.readLock().lock();

		try {
			return super.viewLocations(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> viewPositions(String word, String location) {
		lock.readLock().lock();

		try {
			return super.viewPositions(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void writeWordCountMap(Path output) throws IOException {
		lock.readLock().lock();

		try {
			super.writeWordCountMap(output);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void writeIndexMap(Path output) throws IOException {
		lock.readLock().lock();

		try {
			super.writeIndexMap(output);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<IndexSearcher> search(TreeSet<String> queries, boolean isPartial) {
		lock.readLock().lock();
		try {
			return super.search(queries, isPartial);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<IndexSearcher> exactSearch(TreeSet<String> queries) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<IndexSearcher> partialSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		}
		finally {
			lock.readLock().unlock();
		}
	}

}
