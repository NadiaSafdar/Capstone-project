package com.afroexaentric.Appointment.AppointmentHistory;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import com.afroexaentric.R;

public class AppointmentHistory_Dialoge {
    private Activity activity;
     AlertDialog alertDialog;

    public AppointmentHistory_Dialoge(Activity activity) {
        this.activity = activity;
    }

    public void dialogueGlovie_Image(String strdata) {

        try {
            JSONObject jsonObject = new JSONObject(strdata);
            String strTitle = "Glovie";
            alertDialog = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle).create();

            LayoutInflater inflater = activity.getLayoutInflater();
            final View view = inflater.inflate(R.layout.appointment_dialoge, null);

            alertDialog.setView(view);

            TextView tvServiceName = (TextView) view.findViewById(R.id.tv_ServiceName);
            TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
            TextView tvTime_Slot = (TextView) view.findViewById(R.id.tv_TimeSlot);
            TextView tvDuration = (TextView) view.findViewById(R.id.tv_Duration);
            TextView tvPrice = (TextView) view.findViewById(R.id.tv_Price);
            TextView tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            TextView tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            TextView tvTotalPrice = (TextView) view.findViewById(R.id.tvTotalPrice);
            TextView tvTotalAmount = (TextView) view.findViewById(R.id.tvTotal_Amount);

            final Button btnDownload=(Button)view.findViewById(R.id.btnDownload);

            ImageView imgCancel=(ImageView)view.findViewById(R.id.imgDelete);

            tvDate.setText(jsonObject.getString("requested_date"));
            tvDate.setText(jsonObject.getString("requested_date"));
            tvTime_Slot.setText(jsonObject.getString("requested_time"));
            tvDuration.setText(jsonObject.getString("duration"));
            tvPrice.setText("$" + jsonObject.getString("total"));
            tvLocation.setText(jsonObject.getString("location_type").equalsIgnoreCase("1") ? "Inside Office" : "OutSideOffice");
            tvStatus.setText(jsonObject.getString("status").equalsIgnoreCase("1") ? "Pending" : "Accepted");
            tvTotalPrice.setText("$" + jsonObject.getString("total"));
            tvTotalAmount.setText("$" + jsonObject.getString("total"));

            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    btnDownload.setVisibility(View.GONE);
                    new Handler().postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            captureScreen(btnDownload);
                            btnDownload.setVisibility(View.VISIBLE);
                            Toast.makeText(activity, "Saved to your memory", Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);

                }
            });

            imgCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void captureScreen(View view) {

        View v = view.getRootView();
        v.setDrawingCacheEnabled(true);
        //Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        Bitmap bmp=screenShot(v);
        v.setDrawingCacheEnabled(false);
        try {
            FileOutputStream fos = new FileOutputStream(new File(Environment
                    .getExternalStorageDirectory().toString(), "SCREEN"
                    + System.currentTimeMillis() + ".png"));
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
