package com.example.mymoviedb.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviedb.DetailActivity;
import com.example.mymoviedb.R;
import com.example.mymoviedb.models.MediaItem;
import com.example.mymoviedb.utils.SharedPreference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.MediaViewHolder> {
    private Context mContext;
    private ArrayList<MediaItem> mediaList;


//    private SharedPreferences prefs;
//    private SharedPreferences.Editor editor;

    public static final String mypreference = "mypref";
    SharedPreference sharedPreference;
    List<MediaItem> watchlist;
    private String className;

    public MediaListAdapter(Context context, ArrayList<MediaItem> mediaList, String className) {
        mContext = context;
        this.mediaList = mediaList;
        this.className = className;

        sharedPreference = new SharedPreference();
        watchlist = sharedPreference.getFavorites(mContext);
        if (watchlist == null) {
            watchlist = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d("!!!", mContext.getClass().getSimpleName());
        View v = v = LayoutInflater.from(mContext).inflate(R.layout.card_layout, parent, false);
        if (className.equals("DetailActivity")) {
             v = LayoutInflater.from(mContext).inflate(R.layout.card_layout_recommendation, parent, false);
        }
        return new MediaListAdapter.MediaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaItem currentItem = mediaList.get(position);

        String id = currentItem.getId();
        String type = currentItem.getType();
        String title = currentItem.getTitle();
        String posterUrl = currentItem.getPosterUrl();


        Picasso.get().load(posterUrl).placeholder(R.drawable.poster_path_placeholder).fit().centerCrop().into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "Click..." + position, Toast.LENGTH_SHORT).show();

                Intent detailIntent = new Intent(mContext, DetailActivity.class);
                MediaItem clickedItem = mediaList.get(position);

                detailIntent.putExtra("id", clickedItem.getId());
                detailIntent.putExtra("type", clickedItem.getType());
                detailIntent.putExtra("title", clickedItem.getTitle());
                detailIntent.putExtra("posterUrl", clickedItem.getPosterUrl());

                mContext.startActivity(detailIntent);
            }
        });

        if (!className.equals("DetailActivity")) {
            holder.popUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(mContext, holder.popUpBtn);


                    // Inflating popup menu from popup_menu.xml file
                    if (checkFavoriteItem(new MediaItem(id, type, title, posterUrl))) {
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_remove, popupMenu.getMenu());
                    } else {
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                    }

                    // TODO cannot find this item
//                MenuItem item = holder.imageView.findViewById(R.id.popup_watchlist);
//                if (!checkFavoriteItem(new MediaItem(id, type, title, posterUrl))) {
//                    item.setTitle("Add to Watchlist");
//                } else {
//                    item.setTitle("Remove from Watchlist");
//                }


                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            // Toast message on menu item clicked
//                        Toast.makeText(mContext, "You Clicked " + title, Toast.LENGTH_SHORT).show();


                            if (menuItem.getTitle().equals("Open in TMDB")) {
//                            SharedPreferences settings = v.getContext().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
//                            editor.clear().commit();
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://themoviedb.org/" + type.substring(0, type.length() - 1) + "/" + id));
                                mContext.startActivity(browserIntent);
                            }


                            if (menuItem.getTitle().equals("Share on Facebook")) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fthemoviedb.org%2F" + type.substring(0, type.length() - 1) + "%2F" + id + "&amp;src=sdkpreparse\n"));
                                mContext.startActivity(browserIntent);
                            }

                            if (menuItem.getTitle().equals("Share on Twitter")) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://twitter.com/intent/tweet?text=Check%20this%20out&url=https%3A%2F%2Fwww.themoviedb.org%2F" + type.substring(0, type.length() - 1) + "%2F" + id));
                                mContext.startActivity(browserIntent);
                            }


                            if (menuItem.getTitle().equals("Remove from Watchlist")) {
                                removeFromWatchlist(new MediaItem(id, type, title, posterUrl));
                                Toast.makeText(mContext, title + " was removed from Watchlist", Toast.LENGTH_SHORT).show();
                            }

                            if (menuItem.getTitle().equals("Add to Watchlist")) {
                                addToWatchlist(new MediaItem(id, type, title, posterUrl));
                                Toast.makeText(mContext, title + " was added to Watchlist", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    // Showing the popup menu
                    popupMenu.show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView popUpBtn;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            popUpBtn = itemView.findViewById(R.id.popUp_btn);
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
//        super.add(product);
        watchlist.add(media);
        sharedPreference.addFavorite(mContext, media);
//        notifyDataSetChanged();
    }

    public void removeFromWatchlist(MediaItem media) {
//        super.remove(product);
        watchlist.remove(media);
        sharedPreference.removeFavorite(mContext, media);
//        notifyDataSetChanged();
    }
}
