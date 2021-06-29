package com.example.mymoviedb.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviedb.DetailActivity;
import com.example.mymoviedb.R;
import com.example.mymoviedb.models.MediaItem;
import com.example.mymoviedb.utils.ItemMoveCallback;
import com.example.mymoviedb.utils.SharedPreference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.MediaViewHolder> implements ItemMoveCallback.ItemTouchHelperContract{
    private Context mContext;

    SharedPreference sharedPreference;
    ArrayList<MediaItem> watchlist;

    private TextView nothingInWatchlist;

    public MediaGridAdapter(Context context, TextView textView) {
        mContext = context;
        nothingInWatchlist = textView;

        sharedPreference = new SharedPreference();
        watchlist = sharedPreference.getFavorites(mContext);
        if (watchlist == null) {
            watchlist = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_layout_watchlist, parent, false);
        return new MediaGridAdapter.MediaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaItem currentItem = watchlist.get(position);

        String id = currentItem.getId();
        String type = currentItem.getType();
        String title = currentItem.getTitle();
        String posterUrl = currentItem.getPosterUrl();

        holder.type.setText(type.substring(0, type.length() - 1));
        Picasso.get().load(posterUrl).placeholder(R.drawable.poster_path_placeholder).fit().centerCrop().into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "Click..." + position, Toast.LENGTH_SHORT).show();

                Intent detailIntent = new Intent(mContext, DetailActivity.class);
                MediaItem clickedItem = watchlist.get(position);

                detailIntent.putExtra("id", clickedItem.getId());
                detailIntent.putExtra("type", clickedItem.getType());

                mContext.startActivity(detailIntent);
            }
        });

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromWatchlist(new MediaItem(id, type, title, posterUrl));
                Toast.makeText(mContext, "\"" + title + "\" was removed from favourites" , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return watchlist.size();
    }



    public class MediaViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView removeBtn;
        public TextView type;

        View rowView;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);

            rowView = itemView;

            imageView = itemView.findViewById(R.id.image_view);
            removeBtn = itemView.findViewById(R.id.watchlist_removeBtn);
            type = itemView.findViewById(R.id.watchlist_mediaType);
        }
    }


    /*Checks whether a particular tv/movie exists in SharedPreferences*/
    public boolean checkFavoriteItem(MediaItem media) {
        boolean check = false;
        List<MediaItem> favorites = sharedPreference.getFavorites(mContext);
        if (favorites != null) {
            for (MediaItem favorite : favorites) {
                if (favorite.equals(media)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    public void addToWatchlist(MediaItem media) {
        watchlist.add(media);
        sharedPreference.addFavorite(mContext, media);
        notifyDataSetChanged();
    }

    public void removeFromWatchlist(MediaItem media) {
        watchlist.remove(media);
        sharedPreference.removeFavorite(mContext, media);

        if (watchlist.size() == 0) {
            nothingInWatchlist.setVisibility(View.VISIBLE);
        }

        notifyDataSetChanged();

    }


    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(watchlist, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(watchlist, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

        //TODO save changes to sharedPreference
        sharedPreference.saveFavorites(mContext, watchlist);
    }

    public void onRowSelected(MediaViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);

    }

    public void onRowClear(MediaViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);

    }
}
