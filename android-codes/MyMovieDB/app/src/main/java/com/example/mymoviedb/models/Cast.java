package com.example.mymoviedb.models;

public class Cast {
    private String name;
    private String profileImgSrc;

    public Cast(String name, String profileImgSrc) {
        this.name = name;
        this.profileImgSrc = profileImgSrc;
    }

    public String getName() {
        return name;
    }

    public String getProfileImgSrc() {
        return profileImgSrc;
    }
}
