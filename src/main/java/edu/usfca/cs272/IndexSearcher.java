package edu.usfca.cs272;

import java.nio.file.Path;

/**
 * Represents a search result in the inverted index, including the count of matches, score, and document path.
 */
public class IndexSearcher implements Comparable<IndexSearcher> {
	
	/** The count of matches. */
	public int count;
	
	/** The score of the search result. */
	public String score;
	
	/** The path of the document containing the matches. */
	public Path where;
	
	/**
     * Constructs an IndexSearcher object with the given parameters.
     *
     * @param count The count of matches.
     * @param score The score of the search result.
     * @param where The path of the document containing the matches.
     */
	public IndexSearcher(int count, String score, Path where) {
		this.count = count;
		this.score = score;
		this.where = where;
	}
	
	/**
	 * Retrieves the count of matches.
	 *
	 * @return The count of matches.
	 */
	public int getCount() {
		return count;
	}
	
	/**
	* Adds the specified value to the count of matches.
	*
	* @param c The value to add to the count of matches.
	*/
	public void addCount(int c) {
		this.count += c;
	}
	
	/**
	 * Sets the count of matches to the specified value.
	 *
	 * @param c The value to set as the count of matches.
	 */
	public void setCount(int c) {
		this.count = c;
	}
	
	/**
	 * Retrieves the score of the search result.
	 *
	 * @return The score of the search result.
	 */
	public String getScore() {
		return score;
	}
	
	/**
     * Retrieves the path of the document containing the matches.
     *
     * @return The path of the document containing the matches.
     */
	public Path getWhere() {
		return where;
	}
	
	/**
	 * Sets the score of the search result.
	 *
	 * @param score The score to set.
	 */
	public void setScore(String score) {
		this.score = score;
	}
	
	/**
	 * Sets the path of the document containing the matches.
	 *
	 * @param where The path to set.
	 */
	public void setWhere(Path where) {
		this.where = where;
	}
	
	/**
	 * Compares this IndexSearcher with another IndexSearcher for sorting.
	 *
	 * @param other The IndexSearcher to compare with.
	 * @return A negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(IndexSearcher other) {
		int scoreComparison = Double.compare(Double.parseDouble(other.getScore()), Double.parseDouble(this.getScore()));
		if (scoreComparison != 0)
			return scoreComparison;
		
		int countComparison = Integer.compare(other.getCount(), this.getCount());
		if (countComparison != 0)
			return countComparison;
		
		return this.getWhere().toString().compareToIgnoreCase(other.getWhere().toString());
	}
	
	/**
	 * Returns a string representation of the IndexSearcher object.
	 *
	 * @return A string representation of the IndexSearcher object.
	 */
	@Override
	public String toString() {
		return "{" +
			"\"count\":" + count +
			", \"score\":" + score +
			", \"where\":\"" + where + "\"" +
			"}";
	}

}