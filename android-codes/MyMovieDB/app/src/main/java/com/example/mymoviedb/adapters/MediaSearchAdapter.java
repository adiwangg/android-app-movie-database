package com.example.mymoviedb.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviedb.DetailActivity;
import com.example.mymoviedb.R;
import com.example.mymoviedb.models.SearchResultItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MediaSearchAdapter extends RecyclerView.Adapter<MediaSearchAdapter.MediaViewHolder> {
    private Context mContext;
    private ArrayList<SearchResultItem> mediaList;


    public MediaSearchAdapter(Context context, ArrayList<SearchResultItem> mediaList) {
        mContext = context;
        this.mediaList = mediaList;
        if (mediaList == null) {
            mediaList = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_searchlist_layout, parent, false);
        return new MediaSearchAdapter.MediaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        SearchResultItem currentItem = mediaList.get(position);

        String id = currentItem.getId();
        String type = currentItem.getType();
        String title = currentItem.getTitle();
        String posterUrl = currentItem.getPosterUrl();
        String year = currentItem.getYear();
        String rating = currentItem.getRating();


        Picasso.get().load(posterUrl).placeholder(R.drawable.backdrop_path_placeholder).fit().centerCrop().into(holder.posterBackground);
        holder.typeAndYear.setText(type + " (" + year + ")");
        holder.rating.setText(rating);
        holder.title.setText(title);



//        Spannable span = new SpannableString(title);
//        span.setSpan(new RelativeSizeSpan(1.5f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        holder.title.setText(span);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "Click..." + position, Toast.LENGTH_SHORT).show();

                Intent detailIntent = new Intent(mContext, DetailActivity.class);
                SearchResultItem clickedItem = mediaList.get(position);

                detailIntent.putExtra("id", clickedItem.getId());
                detailIntent.putExtra("type", clickedItem.getType() + "s");
                detailIntent.putExtra("title", clickedItem.getTitle());
                detailIntent.putExtra("posterUrl", clickedItem.getPosterUrl());

                mContext.startActivity(detailIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterBackground;
        public TextView typeAndYear;
        public TextView rating;
        public TextView title;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            posterBackground = itemView.findViewById(R.id.search_poster_background);
            typeAndYear = itemView.findViewById(R.id.search_type_and_year);
            rating = itemView.findViewById(R.id.search_rating);
            title = itemView.findViewById(R.id.search_title);

        }
    }
}
