package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;

public class QueuedFileProcessor {
	public static void traverseDirectory(Path input, ThreadSafeInvertedIndex index, WorkQueue queue)
			throws IOException, NotDirectoryException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(input);) {

			var iterator = stream.iterator();

			while (iterator.hasNext()) {
				Path newPath = iterator.next();

				if (Files.isDirectory(newPath)) {
					traverseDirectory(newPath, index, queue);
				}
				else if (Files.isRegularFile(input) && FileProcessor.isExtensionText(newPath)) {
					Task task = new Task(newPath, index);
					queue.execute(task);
				}
			}
		}
	}

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
			}
			catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
