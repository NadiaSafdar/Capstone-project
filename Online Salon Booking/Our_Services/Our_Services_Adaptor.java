package com.afroexaentric.Our_Services;

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

public class Our_Services_Adaptor extends RecyclerView.Adapter<Our_Services_Adaptor.View_Holder_Profile> {

    private Activity activity;
    private SharedPreferences sharedPreferences;
    ArrayList<HashMap<String, String>> list;
    ArrayList<Object> listImages;

    public Our_Services_Adaptor(Activity activity1, ArrayList<HashMap<String, String>> list1) {
        activity = activity1;
        list = list1;

        sharedPreferences = activity.getSharedPreferences(Log_Constants.MY_PREFS, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View_Holder_Profile onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        view = LayoutInflater.from(activity).inflate(R.layout.our__services_layout, viewGroup, false);
        return new View_Holder_Profile(view);
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder_Profile holder_profile, int position) {

        HashMap<String, String> hashMap = list.get(position );
        Picasso.with(activity).load(hashMap.get(Our_Services.IMAGE_URL)).placeholder(R.drawable.logo).into(holder_profile.img);
        holder_profile.tvName.setText(hashMap.get(Our_Services.SERVICE_NAME));

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class View_Holder_Profile extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        ImageView img;
        LinearLayout layout1;

        public View_Holder_Profile(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvService);
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




