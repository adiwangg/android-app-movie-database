package com.example.mymoviedb.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviedb.R;
import com.example.mymoviedb.ReviewDetailActivity;
import com.example.mymoviedb.models.Review;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewListViewHolder> {

    private Context mContext;
    private ArrayList<Review> reviewList;

    public ReviewListAdapter(Context context, ArrayList<Review> reviewList) {
        mContext = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_layout_reviews, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListViewHolder holder, int position) {
        Review currentReview = reviewList.get(position);

        String name = currentReview.getName();
        String creationTime = currentReview.getCreationTime();
        Double voting = currentReview.getVoting();
        String content = currentReview.getContent();

        holder.nameAndTime.setText("by " + name + " on " + creationTime);
        holder.voting.setText(voting + "/5 ");
        holder.content.setText(content);

        // TODO 点击跳转
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailIntent = new Intent(mContext, ReviewDetailActivity.class);
                Review clickedItem = reviewList.get(position);

                detailIntent.putExtra("voting", clickedItem.getVoting());
                detailIntent.putExtra("name", clickedItem.getName());
                detailIntent.putExtra("creationTime", clickedItem.getCreationTime());
                detailIntent.putExtra("content", clickedItem.getContent());

                mContext.startActivity(detailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    class ReviewListViewHolder extends RecyclerView.ViewHolder {

        private TextView nameAndTime;
        private TextView voting;
        private TextView content;

        public ReviewListViewHolder(View itemView) {
            super(itemView);
            nameAndTime = itemView.findViewById(R.id.review_nameAndTime);
            voting = itemView.findViewById(R.id.review_voting);
            content = itemView.findViewById(R.id.review_content);
        }
    }
}
