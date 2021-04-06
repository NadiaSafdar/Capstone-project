package com.afroexaentric.Our_Services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import com.afroexaentric.Comman_Stuffs.Gson_List_Prefs;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.R;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Add_Cart_Adaptor extends RecyclerView.Adapter<Add_Cart_Adaptor.ViewHolder> {
    private Activity activity;
    private ArrayList<HashMap<String, String>> listnotifications;
    private SharedPreferences sharedPreferences;

    public Add_Cart_Adaptor(FragmentActivity notification_activity, ArrayList<HashMap<String, String>> listnoti) {
        activity = notification_activity;
        listnotifications = listnoti;
        sharedPreferences = activity.getSharedPreferences(Log_Constants.MY_PREFS, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_cart_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        try {

            HashMap<String, String> map = listnotifications.get(position);

            Picasso.with(activity).load(map.get(Sub_Service.IMAGE_URL)).placeholder(R.drawable.logo).into(viewHolder.imgAddcart);
            viewHolder.tvName.setText(map.get(Sub_Service.SERVICE_NAME));

        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return listnotifications.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  tvName;
        CircleImageView imgAddcart;
        ImageView imgDelete;

        CardView cvRoot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);

            imgAddcart = (CircleImageView) itemView.findViewById(R.id.imgUser);
            imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);

            imgDelete.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            final int pos = getAdapterPosition();
            if (view.getId() == R.id.imgDelete) {
                final Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_out_right);
                view.startAnimation(animation);
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Addcart value is incrementing when add button is clicked
                        //set Addcart value is 1 when removing item form list(default qty value is 1)
                        listnotifications.get(pos).put(Sub_Service.ADDCART_VALUE, "1");

                        new Gson_List_Prefs(activity).saveItem(listnotifications.get((pos)));

                      //  Log.d("json Add selectCount[i]", String.valueOf(Service_Add_Data.selectedCount.get(pos)));

                       // Service_Add_Data.selectedCount.remove(pos);
                        listnotifications.remove(pos);//remove item form list
                        notifyDataSetChanged();
                        animation.cancel();

                        //if there is no product then show textview
                        if (listnotifications.isEmpty()) {
                            Toast.makeText(activity, "No Services available", Toast.LENGTH_SHORT).show();
                            activity.finish();
                            //  Service_Add_Data.tvNodata.setText("There are no products in your cart.");
                            // Service_Add_Data.tvNodata.setVisibility(View.VISIBLE);
                        }

                    }
                }, 100);
            }
        }
    }
}

