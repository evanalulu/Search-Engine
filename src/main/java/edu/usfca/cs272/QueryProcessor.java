package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.usfca.cs272.InvertedIndex.IndexSearcher;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A class responsible for processing queries and managing search results.
 */
public class QueryProcessor implements QueryProcessorInterface {

	/**
	 * The inverted index used for query processing and search result management.
	 */
	private final InvertedIndex index;

	/**
	 * A boolean flag indicating whether the search mode is set to partial (true) or
	 * exact (false).
	 */
	private final boolean isPartial;

	/**
	 * The stemmer used for stemming words.
	 */
	private final Stemmer stemmer;

	/**
	 * The map storing search results, where keys represent query strings and values
	 * represent lists of searchers.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.IndexSearcher>> searchResult;

	/**
	 * Constructs a QueryProcessor with the specified inverted index, search mode,
	 * and stemmer.
	 *
	 * @param index the inverted index to be used for query processing
	 * @param isPartial a boolean indicating whether to use partial search (true) or
	 *   exact search (false)
	 */
	public QueryProcessor(InvertedIndex index, boolean isPartial) {
		this.index = index;
		this.isPartial = isPartial;
		this.stemmer = new SnowballStemmer(ENGLISH);
		this.searchResult = new TreeMap<>();
	}

	@Override
	public void processQueries(String line) {
		TreeSet<String> query = FileStemmer.uniqueStems(line, stemmer);
		String queryString = String.join(" ", query);

		if (queryString.isEmpty() || searchResult.containsKey(queryString)) {
			return;
		}

		ArrayList<InvertedIndex.IndexSearcher> results = index.search(query, isPartial);
		searchResult.put(queryString, results);
	}

	@Override
	public int numQueryLines() {
		return viewQueries().size();
	}

	@Override
	public int numResults(String query) {
		return viewResults(query).size();
	}

	@Override
	public boolean hasQueryLine(String queryLine) {
		return viewQueries().contains(getQueryString(queryLine));
	}

	@Override
	public Set<String> viewQueries() {
		return Collections.unmodifiableSet(searchResult.keySet());
	}

	@Override
	public List<IndexSearcher> viewResults(String query) {
		ArrayList<IndexSearcher> searchers = searchResult.get(getQueryString(query));
		return (searchers != null) ? Collections.unmodifiableList(searchers) : Collections.emptyList();
	}

	@Override
	public String getQueryString(String query) {
		return String.join(" ", FileStemmer.uniqueStems(query, stemmer));
	}

	@Override
	public void writeSearchResults(Path output) throws IOException {
		JsonWriter.writeSearchResults(searchResult, output);
	}
}
