package com.afroexaentric.Gallery;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.R;

public class Gallery_Collections_Adaptor extends RecyclerView.Adapter<Gallery_Collections_Adaptor.View_Holder_Profile> {

    private Activity activity;
    private SharedPreferences sharedPreferences;
    ArrayList<HashMap<String, String>> list;
    public static final int ONE_ROW = 1;
    public static final int TWO_ROW = 2;

    public Gallery_Collections_Adaptor(Activity activity1, ArrayList<HashMap<String, String>> list1) {
        activity = activity1;
        list = list1;

        sharedPreferences = activity.getSharedPreferences(Log_Constants.MY_PREFS, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View_Holder_Profile onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;

        if (viewType == ONE_ROW) {
            view = LayoutInflater.from(activity).inflate(R.layout.gallery_collections_layout, viewGroup, false);
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.our__services_layout_two, viewGroup, false);
        }
        return new View_Holder_Profile(view);
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder_Profile holder_profile, int position) {

         HashMap<String, String> hashMap = list.get(position);
         Picasso.with(activity).load(hashMap.get(Gallery_Collections.IMAGE_URL)).placeholder(R.drawable.logo).into(holder_profile.img);
//        if (getItemViewType(position) == ONE_ROW) {
//            holder_profile.img.setImageResource(R.drawable.girl_landscape);
//        }else {
//            holder_profile.img.setImageResource(R.drawable.girl_square);
//        }

    }


    @Override
    public int getItemCount() {

        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
       // if (list.get(position).get(Gallery_Collections.PDF_URL).equalsIgnoreCase("0")){
        if (position%5==0){
            return ONE_ROW;
        }
        else {
            return TWO_ROW;
        }
    }

    public class View_Holder_Profile extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        ImageView img;
        LinearLayout layout1;

        public View_Holder_Profile(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            img = (ImageView) itemView.findViewById(R.id.img);
            layout1 = (LinearLayout) itemView.findViewById(R.id.layout_rv_Profile);

            // layout1.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.layout_rv_Profile:
//
//                    new Open_External_URl(activity).openLinkIn_Browser(list.get(getAdapterPosition()).get(My_Vehicle_info_Detail.PDF_URL));
//                    break;

            }
        }

    }
}





