package com.example.mymoviedb.adapters;

import android.content.Intent;
import android.view.ViewGroup;

import com.example.mymoviedb.DetailActivity;
import com.example.mymoviedb.R;
import com.example.mymoviedb.models.MediaItem;
import com.smarteist.autoimageslider.SliderViewAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {
    // list for storing urls of images.
//    private final List<SliderData> mSliderItems;
    private Context mContext;
    private ArrayList<MediaItem> mediaList;

    // Constructor
    public SliderAdapter(Context context, ArrayList<MediaItem> sliderDataArrayList) {
        this.mediaList = sliderDataArrayList;
        mContext = context;
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
        return new SliderAdapterViewHolder(inflate);
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final MediaItem sliderItem = mediaList.get(position);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(mContext, DetailActivity.class);
                MediaItem clickedItem = mediaList.get(position);

                detailIntent.putExtra("id", clickedItem.getId());
                detailIntent.putExtra("type", clickedItem.getType());
                detailIntent.putExtra("title", clickedItem.getTitle());
                detailIntent.putExtra("posterUrl", clickedItem.getPosterUrl());

                mContext.startActivity(detailIntent);
            }
        });

        // Glide is use to load image from url in your imageview.
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getPosterUrl())
                .centerCrop()
                .transform(new jp.wasabeef.glide.transformations.BlurTransformation(25, 2))
                .into(viewHolder.blurredImg);

        Glide.with(viewHolder.itemView)
                .load(sliderItem.getPosterUrl())
                .centerCrop()
                .into(viewHolder.imageViewBackground);

    }


    // this method will return the count of our list.
    @Override
    public int getCount() {
        return mediaList.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        // Adapter class for initializing the views of our slider view.
        View itemView;
        ImageView imageViewBackground;
        ImageView blurredImg;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.myimage);
            this.blurredImg = itemView.findViewById(R.id.myBlurImage);
            this.itemView = itemView;
        }
    }
}
