package com.afroexaentric.Reviews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.afroexaentric.R;

public class Review_Service_Adaptor extends RecyclerView.Adapter<Review_Service_Adaptor.ViewHolder> {
    private ArrayList<HashMap<String, String>> list;

    private Activity context;
    private SharedPreferences sharedPreferences;
    private int pos;

    public Review_Service_Adaptor(FragmentActivity activity, ArrayList<HashMap<String, String>> list) {
        context = activity;
        this.list = list;
        
    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_service_layout, parent, false);

        return new ViewHolder(view);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("json", "onBindViewHolder: " + position);

        HashMap<String, String> hashMap = list.get(position);
        holder.tvName.setText(hashMap.get(Review_Service.NAME));
        holder.tvReview.setText(hashMap.get(Review_Service.REVIEWS));

        Picasso.with(context).load(hashMap.get(Review_Service.IMAGE_URL)).placeholder(R.color.lightgrey).into(holder.imgUser);
        holder.ratingBar.setRating(Float.parseFloat(hashMap.get(Review_Service.RATING)));

    }

    /// recycle view item
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout cvRoot;
        private ImageView imgUser;
        private RatingBar ratingBar;
        private TextView tvName,tvReview;
        private int pos;

        public ViewHolder(View itemView) {
            super(itemView);
            imgUser=(ImageView)itemView.findViewById(R.id.imgUser);
            ratingBar=(RatingBar) itemView.findViewById(R.id.ratingBar);
            tvName=(TextView) itemView.findViewById(R.id.tvName);
            tvReview=(TextView) itemView.findViewById(R.id.tv_Reviews);

        }

        @Override
        public void onClick(View view) {
            pos = getAdapterPosition();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.d("json", "onClick: " + pos);
            switch (view.getId()) {

                default:
                    break;
            }
        }

    }

}









