package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A thread-safe version of {@link FileProcessor} using a work queue.
 * 
 */
public class QueuedFileProcessor {
	/**
	 * Recursively traverses the specified directory and processes each file. For
	 * each regular file with a ".txt" extension, it reads the file and updates the
	 * inverted index. If a file path is already present in the inverted index, it
	 * updates the word count and index information.
	 *
	 * @param input the path to the directory to traverse
	 * @param index the thread-safe inverted index to update with file contents
	 * @param queue the work queue for executing file processing tasks
	 * @throws IOException if an I/O error occurs while traversing the directory or
	 *   processing files
	 */
	public static void traverseDirectory(Path input, ThreadSafeInvertedIndex index, WorkQueue queue) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input);) {

			var iterator = stream.iterator();

			while (iterator.hasNext()) {
				Path newPath = iterator.next();

				if (Files.isDirectory(newPath)) {
					traverseDirectory(newPath, index, queue);
				}
				else if (Files.isRegularFile(newPath) && FileProcessor.isExtensionText(newPath)) {
					Task task = new Task(newPath, index);
					queue.execute(task);
				}
			}
		}
	}

	/**
	 * Processes the specified input path, either as a directory or a single file.
	 *
	 * @param input the path to the directory or file to be processed
	 * @param index the thread-safe inverted index to update with file contents
	 * @param queue the work queue for executing file processing tasks
	 * @throws IOException if an I/O error occurs while processing the input path
	 */
	public static void processPath(Path input, ThreadSafeInvertedIndex index, WorkQueue queue) throws IOException {
		if (Files.isDirectory(input)) {
			traverseDirectory(input, index, queue);
		}
		else {
			Task task = new Task(input, index);
			queue.execute(task);
		}

		queue.finish();
	}

	/**
	 * A task representing the processing of a single file. When executed, it
	 * processes the contents of the file using a non-thread-safe InvertedIndex
	 * object, then adds the processed data to the thread-safe inverted index
	 * provided.
	 */
	private static class Task implements Runnable {
		/**
		 * The path to the file being processed.
		 */
		private final Path path;

		/**
		 * The non-thread-safe inverted index used to process the contents of the file.
		 */
		private final InvertedIndex index;

		/**
		 * The thread-safe inverted index where the processed data will be added.
		 */
		private final ThreadSafeInvertedIndex threadSafeIndex;

		/**
		 * Constructs a Task with the specified file path and thread-safe inverted
		 * index.
		 *
		 * @param path the path to the file to be processed
		 * @param threadSafeIndex the thread-safe inverted index to add the processed
		 *   data to
		 */
		private Task(Path path, ThreadSafeInvertedIndex threadSafeIndex) {
			this.path = path;
			this.threadSafeIndex = threadSafeIndex;
			this.index = new InvertedIndex();
		}

		/**
		 * Processes the contents of the file using a non-thread-safe InvertedIndex
		 * object, then adds the processed data to the thread-safe inverted index.
		 */
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
