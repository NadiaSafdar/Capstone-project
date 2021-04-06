package com.afroexaentric.Appointment.AppointmentHistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afroexaentric.Appointment.Appointment_Constant;
import com.afroexaentric.Comman_Stuffs.Multicolor_Textview;
import com.afroexaentric.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Booking_Appointment_View_Adaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<HashMap<String, String>> list;

    private Activity activity;
    private SharedPreferences sharedPreferences;
    public int pos;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public Booking_Appointment_View_Adaptor(FragmentActivity activity, ArrayList<HashMap<String, String>> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size()+ 1;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;

        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        //// Inflating headerview
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_appointment_view_header, parent, false);
            return new ViewHolder_Header(view);
            //Inflating footerview
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_dialoge, parent, false);

            return new ViewHolder(view);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("json", "onBindViewHolder: " + position);
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;

            HashMap<String, String> map = list.get(position-1);
            viewHolder.tvServiceName.setText(map.get(Pending_Appointment.SERVICE_NAME));
            viewHolder.tvDate.setText(map.get(Pending_Appointment.DATE));
            viewHolder.tvTime_Slot.setText(map.get(Pending_Appointment.DATE));
            viewHolder.tvDuration.setText(map.get(Appointment_Constant.DURATION)+" "+map.get(Appointment_Constant.DURATION_TYPE));
            viewHolder.tvPrice.setText("$" + map.get(Appointment_Constant.PRICE));
            viewHolder.tvLocation.setText(map.get(Appointment_Constant.LOCATION).equalsIgnoreCase("1") ? "At Salon" : "At Your Home");
            viewHolder.tvStatus.setText(map.get(Appointment_Constant.STATUS).equalsIgnoreCase("1") ? "Pending" : "Accepted");
            viewHolder.tvInvoice.setText(map.get(Appointment_Constant.INVOICE_NO));
            if (!map.get(Appointment_Constant.PAYMENT_ID).equalsIgnoreCase("null")){
                new Multicolor_Textview(activity).multiColor(viewHolder.tvInvoice,"Paid  ",map.get(Appointment_Constant.INVOICE_NO),
                        "#128fc7","#000000");
            }


        }
        else {
            ViewHolder_Header viewHolder = (ViewHolder_Header) holder;
            viewHolder.tvSuccessful.setText(list.get(position).get(Appointment_Constant.MESSAGE));
        }

    }

    /// recycle view item
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout cvRoot;
        private TextView tvServiceName,tvDate,tvTime_Slot,tvDuration,tvPrice,tvLocation,tvStatus,tvInvoice;


        public ViewHolder(View itemView) {
            super(itemView);
             tvServiceName = (TextView) itemView.findViewById(R.id.tv_ServiceName);
             tvDate = (TextView) itemView.findViewById(R.id.tvDate);
             tvTime_Slot = (TextView) itemView.findViewById(R.id.tv_TimeSlot);
             tvDuration = (TextView) itemView.findViewById(R.id.tv_Duration);
             tvPrice = (TextView) itemView.findViewById(R.id.tv_Price);
             tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
             tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvInvoice = (TextView) itemView.findViewById(R.id.tvInvoice);
        }

        @Override
        public void onClick(View view) {
            pos = getAdapterPosition();

            Log.d("json", "onClick: " + pos);
            switch (view.getId()) {

                default:
                    break;
            }
        }

    }

    //headerview holder
    public class ViewHolder_Header extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvSuccessful;
        ViewHolder_Header(@NonNull View itemView) {
            super(itemView);
            tvSuccessful=(TextView)itemView.findViewById(R.id.tvBooking_Successful);

        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: " + "click");

        }
    }
}


