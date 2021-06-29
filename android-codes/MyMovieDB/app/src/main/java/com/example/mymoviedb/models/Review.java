package com.example.mymoviedb.models;

public class Review {

    private String name;
    private String creationTime;
    private double voting;
    private String content;

    public Review(String name, String creationTime, double voting, String content) {
        this.name = name;
        this.creationTime = creationTime;
        this.voting = voting;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public double getVoting() {
        return voting;
    }

    public String getContent() {
        return content;
    }

}
