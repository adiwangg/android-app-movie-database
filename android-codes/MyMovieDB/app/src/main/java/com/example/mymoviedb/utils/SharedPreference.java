package com.example.mymoviedb.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.mymoviedb.models.MediaItem;
import com.google.gson.Gson;


public class SharedPreference {

    public static final String PREFS_NAME = "mypref";
    public static final String FAVORITES = "watchlist";

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<MediaItem> favorites) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, MediaItem product) {
        List<MediaItem> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<MediaItem>();
        favorites.add(product);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, MediaItem product) {
        ArrayList<MediaItem> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(product);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<MediaItem> getFavorites(Context context) {
        SharedPreferences settings;
        List<MediaItem> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            MediaItem[] favoriteItems = gson.fromJson(jsonFavorites,
                    MediaItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<MediaItem>(favorites);
        } else
            return null;

        return (ArrayList<MediaItem>) favorites;
    }
}

