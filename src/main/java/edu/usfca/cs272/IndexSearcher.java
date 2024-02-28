package edu.usfca.cs272;

import java.nio.file.Path;

public class IndexSearcher implements Comparable<IndexSearcher>{
	private String query;
    private int count;
    private double score;
    private Path where;

    // Constructor
    public IndexSearcher(String query, int count, double score, Path where) {
        this.query = query;
        this.count = count;
        this.score = score;
        this.where = where;
    }
    
    // Getters
    public String getQuery() {
        return query;
    }

    public int getCount() {
        return count;
    }

    public double getScore() {
        return score;
    }

    public Path getWhere() {
        return where;
    }

	public void setQuery(String query) {
		this.query = query;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setWhere(Path where) {
		this.where = where;
	}
    



    // Implementing Comparable interface for sorting
    @Override
    public int compareTo(IndexSearcher other) {
        return Double.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return "{" +
                "\"" + query + "\":" +
                " {" +
                "\"count\":" + count +
                ", \"score\":" + score +
                ", \"where\":\"" + where + "\"" +
                " }" +
                "}";
    }

}
