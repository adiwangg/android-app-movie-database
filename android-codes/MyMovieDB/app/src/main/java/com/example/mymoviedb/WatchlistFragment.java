package com.example.mymoviedb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviedb.adapters.MediaGridAdapter;
import com.example.mymoviedb.models.MediaItem;
import com.example.mymoviedb.utils.ItemMoveCallback;
import com.example.mymoviedb.utils.SharedPreference;

import java.util.ArrayList;

public class WatchlistFragment extends Fragment {

    SharedPreference sharedPreference;
    ArrayList<MediaItem> watchlist;

    RecyclerView recyclerView_watchlist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreference = new SharedPreference();
        watchlist = sharedPreference.getFavorites(view.getContext());

        TextView nothingInWatchlist = view.findViewById(R.id.tv_nothing_in_watchlist);

        if (watchlist == null || watchlist.size() == 0) {
            nothingInWatchlist.setVisibility(View.VISIBLE);
        } else {
            nothingInWatchlist.setVisibility(View.GONE);
        }

        recyclerView_watchlist = view.findViewById(R.id.rv_grid_watchlist);
        recyclerView_watchlist.setLayoutManager(new GridLayoutManager(getContext(), 3));

        nothingInWatchlist = view.findViewById(R.id.tv_nothing_in_watchlist);

        MediaGridAdapter mAdapter = new MediaGridAdapter(getContext(), nothingInWatchlist);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView_watchlist);

        recyclerView_watchlist.setAdapter(mAdapter);
    }
}
