package com.example.mymoviedb;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mymoviedb.adapters.MediaListAdapter;
import com.example.mymoviedb.adapters.SliderAdapter;
import com.example.mymoviedb.models.MediaItem;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.mymoviedb.MainActivity.backendUrl;


public class HomeFragment extends Fragment {

    private TextView tab_movie;
    private TextView tab_tv;

    private MediaListAdapter mediaListAdapter;
    private RequestQueue mRequestQueue;

    private RecyclerView recyclerView_topRatedMovies;
    private RecyclerView recyclerView_poplarMovies;

    private RecyclerView recyclerView_topRatedTvs;
    private RecyclerView recyclerView_PopularTvs;

    private RelativeLayout progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.VISIBLE);

        mRequestQueue = Volley.newRequestQueue(view.getContext());

        // region movie/tv tab change
        tab_movie = view.findViewById(R.id.tab_movie);
        tab_tv = view.findViewById(R.id.tab_tv);
        LinearLayout linearLayout_movie = view.findViewById(R.id.home_movie);
        LinearLayout linearLayout_tv = view.findViewById(R.id.home_tv);

        tab_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_movie.setVisibility(View.GONE);
                linearLayout_tv.setVisibility(View.VISIBLE);
                tab_tv.setTextColor(Color.WHITE);
                tab_movie.setTextColor(Color.rgb(37, 150, 190));
            }
        });

        tab_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_tv.setVisibility(View.GONE);
                linearLayout_movie.setVisibility(View.VISIBLE);
                tab_movie.setTextColor(Color.WHITE);
                tab_tv.setTextColor(Color.rgb(37, 150, 190));
            }
        });
        //endregion


        // region Now Playing Movies
        getNowPlayingMovies(view);
        //endregion

        // region Top Rated Movies
        recyclerView_topRatedMovies = view.findViewById(R.id.recycler_view_topRatedMovies);
        recyclerView_topRatedMovies.setHasFixedSize(true);

        // mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_topRatedMovies.setLayoutManager(linearLayoutManager);

//        mRequestQueue = Volley.newRequestQueue(view.getContext());
        getTopRatedMovies(view);
        //endregion

        // region Popular Movies
        recyclerView_poplarMovies = view.findViewById(R.id.recycler_view_popularMovies);
        recyclerView_poplarMovies.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_poplarMovies.setLayoutManager(linearLayoutManager);

//        mRequestQueue = Volley.newRequestQueue(view.getContext());
        getPopularMovies();
        //endregion


        //region Trending TV Shows
        getTrendingTvShows(view);
        //endregion

        // region Top Rated TV Shows
        recyclerView_topRatedTvs = view.findViewById(R.id.recycler_view_topRatedTvs);
        recyclerView_topRatedTvs.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_topRatedTvs.setLayoutManager(linearLayoutManager);

        getTopRatedTvShows(view);
        //endregion

        // region Popular TV Shows
        ArrayList<MediaItem> popularTvShows = new ArrayList<>();
        recyclerView_PopularTvs = view.findViewById(R.id.recycler_view_popularTvs);
        recyclerView_PopularTvs.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_PopularTvs.setLayoutManager(linearLayoutManager);

        getPopularTvShows(view);
        //endregion


        //region footer
        TextView footer_TMDB = view.findViewById(R.id.footer_tmdb_link);
        footer_TMDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.themoviedb.org/"));
                startActivity(browserIntent);
            }
        });
        //endregion





    }

    private void getNowPlayingMovies(View view) {
        //String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=97588ddc4a26e3091152aa0c9a40de22&language=en-US&page=1";
        String url = backendUrl + "movies/nowPlayingMovies";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            // we are creating array list for storing our image urls.
//                            ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
                            ArrayList<MediaItem> sliderDataArrayList = new ArrayList<>();

                            // initializing the slider view.
                            SliderView sliderView = view.findViewById(R.id.slider_movie);


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject movie = jsonArray.getJSONObject(i);

                                String id = movie.getString("id");
                                String title = movie.getString("title");
                                String type = "movies";
                                String poster_url = "https://image.tmdb.org/t/p/w500" + movie.getString("poster_path");

//                                sliderDataArrayList.add(new SliderData(poster_url));
                                sliderDataArrayList.add(new MediaItem(id, type, title, poster_url));

                                if (i == 5) {
                                    break;
                                }
//                                popularMoviesList.add(new MediaItem(id, type, title, poster_url));
                            }

                            // passing this array list inside our adapter class.
                            SliderAdapter adapter = new SliderAdapter(getContext(), sliderDataArrayList);

                            // below method is used to set auto cycle direction in left to
                            // right direction you can change according to requirement.
                            sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

                            // below method is used to
                            // setadapter to sliderview.
                            sliderView.setSliderAdapter(adapter);

                            // below method is use to set
                            // scroll time in seconds.
                            sliderView.setScrollTimeInSec(3);

                            // to set it scrollable automatically
                            // we use below method.
                            sliderView.setAutoCycle(true);

                            // to start autocycle below method is used.
                            sliderView.startAutoCycle();


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

    private void getTopRatedMovies(View view) {
        //String url = "https://api.themoviedb.org/3/movie/top_rated?api_key=97588ddc4a26e3091152aa0c9a40de22&language=en-US&page=1";
        String url = backendUrl + "movies/topRatedMovies";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            ArrayList<MediaItem> topRatedMoviesList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject movie = jsonArray.getJSONObject(i);

                                String id = movie.getString("id");
                                String title = movie.getString("title");
                                String type = "movies";
                                String poster_url = "https://image.tmdb.org/t/p/w500" + movie.getString("poster_path");

                                topRatedMoviesList.add(new MediaItem(id, type, title, poster_url));
                            }

                            mediaListAdapter = new MediaListAdapter(getContext(), topRatedMoviesList, "MainActivity");
                            recyclerView_topRatedMovies.setAdapter(mediaListAdapter);

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

    private void getPopularMovies() {
//        String url = "https://api.themoviedb.org/3/movie/popular?api_key=97588ddc4a26e3091152aa0c9a40de22&language=en-US&page=1";
        String url = backendUrl + "movies/popularMovies";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            ArrayList<MediaItem> popularMoviesList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject movie = jsonArray.getJSONObject(i);

                                String id = movie.getString("id");
                                String title = movie.getString("title");
                                String type = "movies";
                                String poster_url = "https://image.tmdb.org/t/p/w500" + movie.getString("poster_path");

                                popularMoviesList.add(new MediaItem(id, type, title, poster_url));
                            }

                            mediaListAdapter = new MediaListAdapter(getContext(), popularMoviesList, "MainActivity");
                            recyclerView_poplarMovies.setAdapter(mediaListAdapter);

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


    private void getTrendingTvShows(View view) {
        //String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=97588ddc4a26e3091152aa0c9a40de22&language=en-US&page=1";
        String url = backendUrl + "tvs/trendingTvs";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            // we are creating array list for storing our image urls.
//                            ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
                            ArrayList<MediaItem> sliderDataArrayList = new ArrayList<>();

                            // initializing the slider view.
                            SliderView sliderView = view.findViewById(R.id.slider_tv);


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tv = jsonArray.getJSONObject(i);

                                String id = tv.getString("id");
                                String title = tv.getString("name");
                                String type = "tvs";
                                String poster_url = "https://image.tmdb.org/t/p/w500" + tv.getString("poster_path");

//                                sliderDataArrayList.add(new SliderData(poster_url));
                                sliderDataArrayList.add(new MediaItem(id, type, title, poster_url));

                                if (i == 5) {
                                    break;
                                }
//                                popularMoviesList.add(new MediaItem(id, type, title, poster_url));
                            }

                            // passing this array list inside our adapter class.
                            SliderAdapter adapter = new SliderAdapter(getContext(), sliderDataArrayList);

                            // below method is used to set auto cycle direction in left to
                            // right direction you can change according to requirement.
                            sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

                            // below method is used to
                            // setadapter to sliderview.
                            sliderView.setSliderAdapter(adapter);

                            // below method is use to set
                            // scroll time in seconds.
                            sliderView.setScrollTimeInSec(3);

                            // to set it scrollable automatically
                            // we use below method.
                            sliderView.setAutoCycle(true);

                            // to start autocycle below method is used.
                            sliderView.startAutoCycle();


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

    private void getTopRatedTvShows(View view) {
        String url = backendUrl + "tvs/topRatedTvs";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            ArrayList<MediaItem> topRatedTvShows = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tv = jsonArray.getJSONObject(i);

                                String id = tv.getString("id");
                                String title = tv.getString("name");
                                String type = "tvs";
                                String poster_url = "https://image.tmdb.org/t/p/w500" + tv.getString("poster_path");

                                topRatedTvShows.add(new MediaItem(id, type, title, poster_url));
                            }

                            mediaListAdapter = new MediaListAdapter(getContext(), topRatedTvShows, "MainActivity");
                            recyclerView_topRatedTvs.setAdapter(mediaListAdapter);

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

    private void getPopularTvShows(View view) {
        String url = backendUrl + "tvs/popularTvs";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            ArrayList<MediaItem> popularTvShows = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tv = jsonArray.getJSONObject(i);

                                String id = tv.getString("id");
                                String title = tv.getString("name");
                                String type = "tvs";
                                String poster_url = "https://image.tmdb.org/t/p/w500" + tv.getString("poster_path");

                                popularTvShows.add(new MediaItem(id, type, title, poster_url));
                            }

                            mediaListAdapter = new MediaListAdapter(getContext(), popularTvShows, "MainActivity");
                            recyclerView_PopularTvs.setAdapter(mediaListAdapter);

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
}
