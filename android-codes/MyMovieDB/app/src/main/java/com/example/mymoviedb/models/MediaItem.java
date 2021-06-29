package com.example.mymoviedb.models;


public class MediaItem {
    private String id;
    private String type;
    private String title;
    private String posterUrl;

    public MediaItem(String id, String type, String title, String posterUrl) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.posterUrl = posterUrl;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = Integer.parseInt(prime * result + getId());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MediaItem other = (MediaItem) obj;
        if (!getId().equals(other.getId())|| !getType().equals(other.getType()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getType() + " [id=" + getId() + ", type=" + getType() + ", title="
                + getTitle() + ", postUrl=" + getPosterUrl() + "]";
    }
}
