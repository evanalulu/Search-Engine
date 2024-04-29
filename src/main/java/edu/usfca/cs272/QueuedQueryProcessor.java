package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

public class QueuedQueryProcessor {
	private final TreeMap<String, ArrayList<ThreadSafeInvertedIndex.IndexSearcher>> searchResults;
	private final ThreadSafeInvertedIndex threadSafeIndex;
	private final WorkQueue queue;

	public QueuedQueryProcessor(ThreadSafeInvertedIndex threadSafeIndex, boolean usePartial, WorkQueue queue) {
		this.searchResults = new TreeMap<>();
		this.threadSafeIndex = threadSafeIndex;
		this.queue = queue;
	}

	private static class Task implements Runnable {
		private final Path path;
		private final InvertedIndex index;
		private final ThreadSafeInvertedIndex threadSafeIndex;

		private Task(Path path, ThreadSafeInvertedIndex threadSafeIndex) {
			this.path = path;
			this.threadSafeIndex = threadSafeIndex;
			this.index = new InvertedIndex();
		}

		@Override
		public void run() {
			try {
				FileProcessor.processPath(path, index);
				threadSafeIndex.addAll(index);
			}
			catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
