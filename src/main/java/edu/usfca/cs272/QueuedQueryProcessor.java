package edu.usfca.cs272;

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
}
