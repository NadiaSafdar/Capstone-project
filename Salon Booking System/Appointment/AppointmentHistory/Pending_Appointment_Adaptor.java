package com.afroexaentric.Appointment.AppointmentHistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afroexaentric.Alert_Dialogue.AlertDialogue;
import com.afroexaentric.Appointment.Appointment_Constant;
import com.afroexaentric.Comman_Stuffs.Comman_Stuff_Interface;
import com.afroexaentric.Comman_Stuffs.Different_Color;
import com.afroexaentric.Comman_Stuffs.IsSelected_Product;
import com.afroexaentric.Our_Services.Sub_Service;
import com.afroexaentric.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Pending_Appointment_Adaptor extends RecyclerView.Adapter<Pending_Appointment_Adaptor.ViewHolder> {
    private ArrayList<HashMap<String, String>> list;

    private Activity activity;
    private SharedPreferences sharedPreferences;
    public int pos;
    private IsSelected_Product isSelected_product;
    private Comman_Stuff_Interface commanStuffInterface;
    public static final int MAX_LINES = 3;
    public Pending_Appointment_Adaptor(FragmentActivity activity, ArrayList<HashMap<String, String>> list, Comman_Stuff_Interface comman_stuff_interface) {
        this.activity = activity;
        this.list = list;
        commanStuffInterface=comman_stuff_interface;
        isSelected_product= new IsSelected_Product(activity);
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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_appointment_layout, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d("json", "onBindViewHolder: " + position);

        final HashMap<String, String> map = list.get(position);
        holder.tvName.setText(map.get(Pending_Appointment.SERVICE_NAME));
        holder.tvDesc.setText(map.get(Pending_Appointment.DESCRIPTION));
        holder.tvDate.setText(map.get(Pending_Appointment.DATE));
        //holder.tvPrice.setText("$"+map.get(Sub_Service.PRICE));
        new Different_Color(activity).multiColor(holder.tvTime,map.get(Pending_Appointment.TIME_AGO)+"",""
        ,"#8e8e8e","#e41077");
       // holder.tvTime.setText(map.get(Sub_Service.TIME)+" Hours ago |"+" Pending");

        Picasso.with(activity).load(map.get(Sub_Service.IMAGE_URL)).placeholder(R.color.lightgrey).into(holder.imgUser);

//        if (map.get(Pending_Appointment.STATUS).equalsIgnoreCase("1")){
//            holder.btnDelete.setVisibility(View.VISIBLE);
//        }else {
//            holder.btnDelete.setVisibility(View.GONE);
//        }
        if (map.get(Pending_Appointment.STATUS).equalsIgnoreCase("3")){
            holder.btnView.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }else {
            holder.btnView.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }

        holder.tvPaid.setVisibility(map.get(Appointment_Constant.PAYMENT_ID).equalsIgnoreCase("null")?View.GONE:View.VISIBLE);

        holder.tvDesc.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {

                see_More_Text(holder.tvDesc,map.get(Sub_Service.DESCRIPTION));
                // makeTextViewResizable(holder.tvDesc, 3, "View More", true);
                Log.d("json", "onBindViewHolder: "+holder.tvDesc.getLineCount());
            }
        });
    }
    private void see_More_Text(TextView tvDesc, String strMore_text) {
        // Past the maximum number of lines we want to display.
        if (tvDesc.getLineCount() > MAX_LINES) {
            int lastCharShown = tvDesc.getLayout().getLineVisibleEnd(MAX_LINES - 1);

            tvDesc.setMaxLines(MAX_LINES);

            String moreString = "More";
            String suffix = "  " + moreString;

            // 3 is a "magic number" but it's just basically the length of the ellipsis we're going to insert
            String actionDisplayText = strMore_text.substring(0, lastCharShown - suffix.length() - 3) + "..." + suffix;

            SpannableString truncatedSpannableString = new SpannableString(actionDisplayText);
            int startIndex = actionDisplayText.indexOf(moreString);
            truncatedSpannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity,R.color.colorPrimary)), startIndex,
                    startIndex + moreString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDesc.setText(truncatedSpannableString);
        }
    }
    /// recycle view item
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout cvRoot;
        private ImageView imgUser,imgCart;
        private TextView tvName,tvDesc,tvTime,tvDate,tvPaid;
        private Button btnDelete,btnView;
        private boolean isCheck=true;

        public ViewHolder(View itemView) {
            super(itemView);
            imgUser=(ImageView)itemView.findViewById(R.id.imgUser);
            imgCart=(ImageView)itemView.findViewById(R.id.img_Cart);

            tvName=(TextView) itemView.findViewById(R.id.tvName);
            tvDesc=(TextView) itemView.findViewById(R.id.tv_Desc);
            tvTime=(TextView) itemView.findViewById(R.id.tv_Time);
            tvDate=(TextView) itemView.findViewById(R.id.tvDate);
            tvPaid=(TextView) itemView.findViewById(R.id.tvPayment_Charge);

            btnDelete=(Button) itemView.findViewById(R.id.btn_Delete);
            btnView=(Button) itemView.findViewById(R.id.btn_View);

            btnDelete.setOnClickListener(this);
            btnView.setOnClickListener(this);
            tvDesc.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            pos = getAdapterPosition();

            Log.d("json", "onClick: " + pos);
            switch (view.getId()) {
                case R.id.btn_Delete:
                    String strDesc = "Are you sure you to want delete this appointment?";
                    String strOk_Btn = "Delete";
                    String strNo_Btn = "Close";
                    String strTitle = "Delete Appointment";

                    new AlertDialogue(activity).dilague_Delete(strTitle, strDesc, strOk_Btn, strNo_Btn, commanStuffInterface);
                    break;
                case R.id.btn_View:
                    commanStuffInterface.comman_Stuff("alertDialoge");

                    break;
                case R.id.tv_Desc:
                    Log.d(TAG, "onClick: view");
                    if (isCheck) {
                        tvDesc.setText(list.get(pos).get(Sub_Service.DESCRIPTION));
                        tvDesc.setMaxLines(200);
                        isCheck = false;
                    } else {
                        tvDesc.setMaxLines(3);
                        see_More_Text(tvDesc,list.get(pos).get(Sub_Service.DESCRIPTION));
                        isCheck = true;
                    }
                    break;
                default:
                    break;
            }
        }

    }

}

