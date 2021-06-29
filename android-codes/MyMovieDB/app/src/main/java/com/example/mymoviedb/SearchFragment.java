package com.example.mymoviedb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.mymoviedb.adapters.MediaSearchAdapter;
import com.example.mymoviedb.models.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.mymoviedb.MainActivity.backendUrl;

public class SearchFragment extends Fragment {

    private SearchView searchView = null;

    private RequestQueue mRequestQueue;
    private RecyclerView recyclerView_search_list;
    ArrayList<SearchResultItem> searchResultItems;

    private TextView noResultText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRequestQueue = Volley.newRequestQueue(view.getContext());

        noResultText = view.findViewById(R.id.search_no_result);


        searchView = view.findViewById(R.id.search_view);
        searchResultItems = new ArrayList<>();

        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setQueryHint("Search movies and TV");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    searchResultItems.clear();
                    Objects.requireNonNull(recyclerView_search_list.getAdapter()).notifyDataSetChanged();
                    noResultText.setVisibility(View.GONE);
                }

                search(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchResultItems.clear();
                Objects.requireNonNull(recyclerView_search_list.getAdapter()).notifyDataSetChanged();
                TextView searchNoResultText = view.findViewById(R.id.search_no_result);
                searchNoResultText.setVisibility(View.GONE);
                Toast t = Toast.makeText(getContext(), "close", Toast.LENGTH_SHORT);
                t.show();
                return false;
            }
        });


        recyclerView_search_list = view.findViewById(R.id.rv_list_searchlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_search_list.setLayoutManager(linearLayoutManager);

    }




    private void search(String term) {
        String url = backendUrl + "search/" + term;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            searchResultItems = new ArrayList<>();


                            if (jsonArray == null || jsonArray.length() == 0) {
                                noResultText.setVisibility(View.VISIBLE);
                            } else {
                                noResultText.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject media = jsonArray.getJSONObject(i);

                                String id = media.getString("id");
                                String title = media.getString("title");
                                String type = media.getString("type");
                                String poster_url = "https://image.tmdb.org/t/p/w500" + media.getString("backdrop_path");
                                String year = media.getString("year");
                                String rating = media.getString("rating");

                                searchResultItems.add(new SearchResultItem(id, type, title, poster_url, rating, year));
                            }

                            MediaSearchAdapter mediaSearchAdapter = new MediaSearchAdapter(getContext(), searchResultItems);
                            recyclerView_search_list.setAdapter(mediaSearchAdapter);


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
