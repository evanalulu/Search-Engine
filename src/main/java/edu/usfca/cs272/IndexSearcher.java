package edu.usfca.cs272;

import java.nio.file.Path;
import java.util.Comparator;

public class IndexSearcher implements Comparable<IndexSearcher> {
    public int count;
    public String score;
    public Path where;

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
    
    public void setCount(int c) {
    	this.count = c;
    }
    
    public String getScore() {
        return score;
    }

    public Path getWhere() {
        return where;
    }

	public void setScore(String score) {
		this.score = score;
	}

	public void setWhere(Path where) {
		this.where = where;
	}
    
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

    @Override
    public String toString() {
        return "{" +
                "\"count\":" + count +
                ", \"score\":" + score +
                ", \"where\":\"" + where + "\"" +
                "}";
    }

}