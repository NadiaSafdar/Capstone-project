package com.afroexaentric.Our_Services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.R;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Service_Add_Time_Adaptor extends RecyclerView.Adapter<Service_Add_Time_Adaptor.ViewHolder> {

    Activity context;
    private ArrayList<HashMap<String, String>> arrayList;

    private   int pos=-1;
    private SharedPreferences sharedPreferences;
    public String strTimeSlot="";

    public Service_Add_Time_Adaptor(Activity activity, ArrayList<HashMap<String, String>> arrMap) {

        context = activity;
        arrayList = arrMap;
        sharedPreferences = context.getSharedPreferences(Log_Constants.MY_PREFS, Context.MODE_PRIVATE);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_add_data_time_layout, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Log.d(TAG, "Drawer_RecycleAdaptor: if " + position);
        HashMap<String, String> map = arrayList.get(position);
        holder.tvName.setText(map.get(Service_Add_Data.TIME_SLOT));
        if (pos==position){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
            holder.tvName.setTextColor(ContextCompat.getColor(context,R.color.white));
        }
        else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.pink));
            holder.tvName.setTextColor(ContextCompat.getColor(context,R.color.black));
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            cardView = (CardView) itemView.findViewById(R.id.cardview);

            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            pos = getAdapterPosition();

            switch (view.getId()) {
                case R.id.cardview:
                    strTimeSlot=arrayList.get(pos).get(Service_Add_Data.TIME_SLOT);
                    notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }
    }



}










