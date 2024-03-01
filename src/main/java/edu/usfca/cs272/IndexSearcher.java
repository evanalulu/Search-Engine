package edu.usfca.cs272;

import java.nio.file.Path;

public class IndexSearcher implements Comparable<IndexSearcher>{
    private int count;
    private String score;
    private Path where;

    // Constructor
    public IndexSearcher(int count, String score, Path where) {
        this.count = count;
        this.score = score;
        this.where = where;
    }

    public int getCount() {
        return count;
    }
    
    public void addCount(int c) {
    	this.count += c;
    }
    
    public String getScore() {
        return score;
    }

    public Path getWhere() {
        return where;
    }

	public void setCount(int count) {
		this.count = count;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public void setWhere(Path where) {
		this.where = where;
	}
    



    // Implementing Comparable interface for sorting
    @Override
    public int compareTo(IndexSearcher other) {
        return Double.compare(Double.parseDouble(other.score), Double.parseDouble(this.score));

    }

    @Override
    public String toString() {
        return "{" +
                "\"count\":" + count +
                ", \"score\":" + score +
                ", \"where\":\"" + where + "\"" +
                "}";
    }

}