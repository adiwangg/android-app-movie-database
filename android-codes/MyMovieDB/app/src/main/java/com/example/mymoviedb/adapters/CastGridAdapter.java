package com.example.mymoviedb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviedb.R;
import com.example.mymoviedb.models.Cast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CastGridAdapter extends RecyclerView.Adapter<CastGridAdapter.CastGridViewHolder> {

    private Context mContext;
    private ArrayList<Cast> castList;

    public CastGridAdapter(Context context, ArrayList<Cast> castList) {
        mContext = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CastGridViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_cast_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CastGridViewHolder holder, int position) {
        Cast currentCast = castList.get(position);

        String name = currentCast.getName();
        String profileImgSrc = currentCast.getProfileImgSrc();

        holder.name.setText(name);
        Picasso.get().load(profileImgSrc).placeholder(R.drawable.profile_path_placeholder).fit().centerInside().into(holder.profileImg);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }


    class CastGridViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private CircleImageView profileImg;

        public CastGridViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cast_name);
            profileImg = itemView.findViewById(R.id.profile_image);
        }
    }
}
