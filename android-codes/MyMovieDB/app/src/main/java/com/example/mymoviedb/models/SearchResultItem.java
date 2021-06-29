package com.example.mymoviedb.models;

public class SearchResultItem {

    private String id;
    private String type;
    private String title;
    private String posterUrl;
    private String rating;
    private String year;

    public SearchResultItem(String id, String type, String title, String posterUrl, String rating, String year) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getRating() {
        return rating;
    }

    public String getYear() {
        return year;
    }
}
