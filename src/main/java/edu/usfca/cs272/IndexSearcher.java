package edu.usfca.cs272;

import java.nio.file.Path;

public class IndexSearcher implements Comparable<IndexSearcher>{
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
    
    public int compare(IndexSearcher first, IndexSearcher second) {
    	double firstScore = Double.parseDouble(first.getScore());
        double secondScore = Double.parseDouble(second.getScore());
        
        int scoreComparison = Double.compare(secondScore, firstScore);
        if (scoreComparison != 0) {
            return scoreComparison;
        }
        
        int countComparison = Integer.compare(second.getCount(), first.getCount());
        if (countComparison != 0)
            return countComparison;
        
        return first.getWhere().toString().compareToIgnoreCase(second.getWhere().toString());
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