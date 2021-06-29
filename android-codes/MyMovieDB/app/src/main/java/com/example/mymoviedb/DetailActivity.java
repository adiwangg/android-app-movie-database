package com.example.mymoviedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mymoviedb.adapters.CastGridAdapter;
import com.example.mymoviedb.adapters.MediaListAdapter;
import com.example.mymoviedb.adapters.ReviewListAdapter;
import com.example.mymoviedb.models.Cast;
import com.example.mymoviedb.models.MediaItem;
import com.example.mymoviedb.models.Review;
import com.example.mymoviedb.utils.SharedPreference;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.mymoviedb.MainActivity.backendUrl;

public class DetailActivity extends AppCompatActivity {

    private RequestQueue mRequestQueue;

    private RecyclerView recyclerView_cast;
    private RecyclerView recyclerView_Review;
    private RecyclerView recyclerView_recommendedPicks;

    private MediaListAdapter mediaListAdapter;

    SharedPreference sharedPreference;
    List<MediaItem> watchlist;

    private RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        progressBar = findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();

        String id = intent.getStringExtra("id");
        String type = intent.getStringExtra("type");
        String title = intent.getStringExtra("title");
        String posterUrl = intent.getStringExtra("posterUrl");

        MediaItem currentMedia = new MediaItem(id, type, title, posterUrl);
        mRequestQueue = Volley.newRequestQueue(this);


        sharedPreference = new SharedPreference();
        watchlist = sharedPreference.getFavorites(this);
        if (watchlist == null) {
            watchlist = new ArrayList<>();
        }

        //region Add/Remove to watchlist
        boolean isAddedToWatchlist = false;
        for (MediaItem mediaItem : watchlist) {
            if (mediaItem.getId().equals(id) && mediaItem.getType().equals(type)) {
                isAddedToWatchlist = true;
                break;
            }
        }

        ImageView add_to_watchlist_btn = findViewById(R.id.detail_add_to_watchlist_btn);
        ImageView remove_from_watchlist_btn = findViewById(R.id.detail_remove_from_watchlist_btn);
        if (isAddedToWatchlist) {
            remove_from_watchlist_btn.setVisibility(View.VISIBLE);
            add_to_watchlist_btn.setVisibility(View.GONE);
        } else {
            remove_from_watchlist_btn.setVisibility(View.GONE);
            add_to_watchlist_btn.setVisibility(View.VISIBLE);
        }

        add_to_watchlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreference.addFavorite(DetailActivity.this, currentMedia);
                remove_from_watchlist_btn.setVisibility(View.VISIBLE);
                add_to_watchlist_btn.setVisibility(View.GONE);
                Toast.makeText(DetailActivity.this, title + " was added to Watchlist", Toast.LENGTH_SHORT).show();
            }
        });

        remove_from_watchlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreference.removeFavorite(DetailActivity.this, currentMedia);
                remove_from_watchlist_btn.setVisibility(View.GONE);
                add_to_watchlist_btn.setVisibility(View.VISIBLE);
                Toast.makeText(DetailActivity.this, title + " was removed from Watchlist", Toast.LENGTH_SHORT).show();
            }
        });
        //endregion

        //region Share to Facebook/Twitter
        ImageView share_facebook_btn = findViewById(R.id.detail_facebook_btn);
        ImageView share_twitter_btn = findViewById(R.id.detail_twitter_btn);

        share_facebook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fthemoviedb.org%2F" + type.substring(0, type.length() - 1) + "%2F" + id + "&amp;src=sdkpreparse\n"));
                startActivity(browserIntent);
            }
        });

        share_twitter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/intent/tweet?text=Check%20this%20out&url=https%3A%2F%2Fwww.themoviedb.org%2F" + type.substring(0, type.length() - 1) + "%2F" + id));
                startActivity(browserIntent);
            }
        });
        //endregion


        getDetailInfo(id, type);

//        getYoutubeVideo(id, type, posterUrl);


        RelativeLayout castBlock = findViewById(R.id.detail_cast_block);
        getCastInfo(id, type, castBlock);

        RelativeLayout reviewBlock = findViewById(R.id.detail_reviews_block);
        getReviewInfo(id, type, reviewBlock);

        //region Recommended Picks
        RelativeLayout recommendationBlock = findViewById(R.id.detail_recommendation_block);
        recyclerView_recommendedPicks = findViewById(R.id.rv_detail_recommendation);
        recyclerView_recommendedPicks.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_recommendedPicks.setLayoutManager(linearLayoutManager);

        getRecommendedPicks(id, type, recommendationBlock);
        //endregion
    }


    private void getYoutubeVideo(String id, String type, String posterUrl) {
        String url = backendUrl + type + "/video/" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String videoId = response.getString("results");

                            YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
                            getLifecycle().addObserver(youTubePlayerView);

                            if (videoId == null || videoId.length() == 0) {
                                youTubePlayerView.setVisibility(View.GONE);

                                ImageView youtubeImage = findViewById(R.id.youtube_image);

                                Glide.with(DetailActivity.this)
                                        .load("https://image.tmdb.org/t/p/w500" + posterUrl)
                                        .placeholder(R.drawable.backdrop_path_placeholder)
                                        .centerCrop()
                                        .into(youtubeImage);
                                youtubeImage.setVisibility(View.VISIBLE);

                            } else {
                                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                    @Override
                                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                        youTubePlayer.cueVideo(videoId, 0);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    private void getDetailInfo(String id, String type) {
        //String url = "https://api.themoviedb.org/3/" + type + "/" + id + "?api_key=97588ddc4a26e3091152aa0c9a40de22&language=en-US&page=1";
        String url = backendUrl + type + "/" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Youtube
                            String backdrop_path = response.getString("backdrop_path");
                            getYoutubeVideo(id, type, backdrop_path);

                            // title
                            String title = response.getString("title");
                            TextView textViewTitle = findViewById(R.id.detail_title);
                            textViewTitle.setText(title);

                            // overview
                            String overview = response.getString("overview");
                            TextView textViewOverview = findViewById(R.id.detail_overview);
                            textViewOverview.setText(overview);


                            // Genres
                            String genres = response.getString("genres");
                            TextView textViewGenres = findViewById(R.id.detail_genres);
                            textViewGenres.setText(genres);

                            // Year
                            String year = response.getString("release_year");
                            TextView textViewYear = findViewById(R.id.detail_year);
                            textViewYear.setText(year);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }


    private void getCastInfo(String id, String type, RelativeLayout castBlock) {
//        String url = "https://api.themoviedb.org/3/movie/popular?api_key=97588ddc4a26e3091152aa0c9a40de22&language=en-US&page=1";
        String url = backendUrl + type + "/cast/" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("cast");
                            ArrayList<Cast> castList = new ArrayList<>();

                            if (jsonArray.length() == 0) {
                                castBlock.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject cast = jsonArray.getJSONObject(i);

                                String name = cast.getString("original_name");
                                String profileImg = "https://image.tmdb.org/t/p/w500" + cast.getString("profile_path");

                                castList.add(new Cast(name, profileImg));

                                if (i == 5) break;
                            }

                            recyclerView_cast = findViewById(R.id.rv_grid_cast);
//                            recyclerView_cast.hasFixedSize();
                            recyclerView_cast.setLayoutManager(new GridLayoutManager(DetailActivity.this, 3));
                            recyclerView_cast.setAdapter(new CastGridAdapter(DetailActivity.this, castList));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    private void getReviewInfo(String id, String type, RelativeLayout reviewBlock) {
//        String url = "https://api.themoviedb.org/3/movie/popular?api_key=97588ddc4a26e3091152aa0c9a40de22&language=en-US&page=1";
        String url = backendUrl + type + "/reviews/" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            ArrayList<Review> reviewList = new ArrayList<>();

                            if (jsonArray.length() == 0) {
                                reviewBlock.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject review = jsonArray.getJSONObject(i);

                                String name = review.getString("author");
                                String creationTime = formatTime(review.getString("created_at"));
                                double voting = review.getDouble("rating");
                                String content = review.getString("content");
                                reviewList.add(new Review(name, creationTime, voting, content));

                                if (i == 2) break;
                            }

                            recyclerView_Review = findViewById(R.id.rv_list_reviews);
                            recyclerView_Review.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
                            recyclerView_Review.setAdapter(new ReviewListAdapter(DetailActivity.this, reviewList));

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    private void getRecommendedPicks(String id, String type, RelativeLayout recommedationBlock) {
//        String url = "https://api.themoviedb.org/3/movie/popular?api_key=97588ddc4a26e3091152aa0c9a40de22&language=en-US&page=1";
        String url = backendUrl + type + "/recommend/" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            ArrayList<MediaItem> recommendedPicksList = new ArrayList<>();

                            if (jsonArray.length() == 0) {
                                recommedationBlock.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject media = jsonArray.getJSONObject(i);

                                String id = media.getString("id");
                                String title = "";
                                if (type.equals("movies")) {
                                    title = media.getString("title");
                                } else {
                                    title = media.getString("name");
                                }
                                String posterUrl = "https://image.tmdb.org/t/p/w500" + media.getString("poster_path");
                                recommendedPicksList.add(new MediaItem(id, type, title, posterUrl));

                                if (i == 9) break;
                            }

                            mediaListAdapter = new MediaListAdapter(DetailActivity.this, recommendedPicksList, "DetailActivity");
                            recyclerView_recommendedPicks.setAdapter(mediaListAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }


    private String formatTime(String time) throws ParseException {
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(time);
        DateFormat df = new SimpleDateFormat("E, MMM dd yyyy");

        return df.format(date);
    }

}