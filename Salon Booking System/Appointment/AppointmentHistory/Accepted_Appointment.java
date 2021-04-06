package com.afroexaentric.Appointment.AppointmentHistory;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afroexaentric.Appointment.Appointment_Constant;
import com.afroexaentric.Comman_Stuffs.Comman_Stuff_Interface;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.LogCalls.LogCalls_Debug;
import com.afroexaentric.Login_SignUp.Login_Contstant;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.afroexaentric.R;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Accepted_Appointment extends Fragment implements Json_Callback, Comman_Stuff_Interface {
    public static final String SERVICE_NAME = "service_name";
    public static final String IMAGE_URL = "image_url";
    public static final String SERVICE_ID = "serviceID";
    public static final String PRICE = "service_price";
    public static final String TIME = "service_time";
    public static final String DESCRIPTION = "service_description";
    public static final String ADDCART_VALUE = "1";

    private Pending_Appointment_Adaptor pending_appointment_adaptor;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String, String>> list;
    private TextView tvNo_Data;

    private Button btnNext;
    private JSONObject jsonObject;
    private SharedPreferences sharedPreferences;

    private GeometricProgressView progressView;

    View view = null;
    private boolean isView;
    private String strBookingId = "", strWebData = "",strMessage = "";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 121;
    private  String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.accepted_appointment, container, false);
            sharedPreferences = getActivity().getSharedPreferences(Log_Constants.MY_PREFS, Context.MODE_PRIVATE);
            Log.d(TAG, "onCreateView: apointed");
            /// Initilzation widgtes
            init_Widgets(view);
            ///call webservices
            call_Webservices();
        } else {
            isView = true;
        }

        return view;

    }

    ///Intializing widgtes
    private void init_Widgets(View view) {
        progressView = (GeometricProgressView) view.findViewById(R.id.progressView);
        tvNo_Data = (TextView) view.findViewById(R.id.tvNo_Data);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        //arraylist
        list = new ArrayList<>();

    }

    ///call webservices
    private void call_Webservices() {
                new Json_Response(getActivity(), progressView, getHashmap_Values("data")).call_Webservices(this, "allbooking");

    }

    ///sending params to webserver
    private HashMap<String, String> getHashmap_Values(String data) {
        strWebData = data;

        HashMap<String, String> hashMap = new HashMap<>();
        if (strWebData.equalsIgnoreCase("delete")) {
            hashMap.put("booking_id", strBookingId);
        }
        hashMap.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, ""));
        hashMap.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
        hashMap.put("status", "2");
        return hashMap;
    }

    //// json response form jsonreposne class
    //implement json Callback interface
    //update_Response will be called when json response is fetched form webservices in json response class
    @Override
    public void update_Response(JSONObject json) {
        jsonObject = json;
        int success;
        try {
            if (jsonObject != null) {
                success = jsonObject.getInt(Network_Stuffs.SUCCESS);
                if (success == 1) {
                    if (strWebData.equalsIgnoreCase("delete")) {
                        //delete glovie, remove item and refresh list
                        list.remove(pending_appointment_adaptor.pos);
                        pending_appointment_adaptor.notifyItemRemoved(pending_appointment_adaptor.pos);
                    } else {
                        //set recyclerview data and call adaptor
                        set_Recyler_View();

                        strMessage = jsonObject.getString("message");
                        tvNo_Data.setText(jsonObject.getString("message"));
                    }
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (strWebData.equalsIgnoreCase("data")) {
                        if (success == 0) {
                            tvNo_Data.setVisibility(View.VISIBLE);
                            tvNo_Data.setText(jsonObject.getString("message"));
                        }
                    } }
            }
        } catch (Exception e) {
            Log.d(TAG, "update_Response: " + e.getMessage());
        }
    }

    //set recyclerview data and call adaptor
    private void set_Recyler_View() {

        try {
            JSONArray jsonArray1 = jsonObject.getJSONArray("userbooking");
            for (int b = 0; b < jsonArray1.length(); b++) {
                JSONObject jsonItem = jsonArray1.getJSONObject(b);

                String strRequestedDate = jsonItem.getString("requested_date") + " " + jsonItem.getString("requested_time");
                String strTime = jsonItem.getString("timeago");
                String strComment=jsonItem.getString("comments");
                JSONArray jsonArray = jsonItem.getJSONArray("subservices");

                HashMap<String, String> map = new HashMap<>();

                map.put(SERVICE_NAME, jsonArray.getJSONObject(0).getString("name"));
                map.put(DESCRIPTION, strComment);
                map.put(IMAGE_URL, Network_Stuffs.BASE_URL + jsonArray.getJSONObject(0).getString("image"));
                map.put(PRICE, jsonItem.getString("total"));
                map.put(SERVICE_ID, jsonItem.getString("id"));
                map.put(Pending_Appointment.TIME_AGO, strTime);
                map.put(Pending_Appointment.DATE, strRequestedDate);
                map.put(Appointment_Constant.PAYMENT_ID,jsonItem.getString(Appointment_Constant.PAYMENT_ID));
                map.put(Pending_Appointment.JSON_ITEM, jsonItem.toString());
                map.put(Pending_Appointment.STATUS, "2");
                map.put(ADDCART_VALUE, "1");
                list.add(map);
                // }
            }

            ///call adaptor
            pending_appointment_adaptor = new Pending_Appointment_Adaptor(getActivity(), list, this);
            recyclerView.setAdapter(pending_appointment_adaptor);
            if (list.size() > 0) {
                 tvNo_Data.setVisibility(View.GONE);
            } else {
                tvNo_Data.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            LogCalls_Debug.d(TAG, "set_Recyler_View: " + e.getMessage());
        }
    }

    ///for delete row of recyclerview, If Y then delete glovie item
    @Override
    public void comman_Stuff(String s) {
        Log.d(TAG, "comman_Stuff: " + s);
        if (s.equalsIgnoreCase("y")) {
            progressView.setVisibility(View.VISIBLE);
            strBookingId = list.get(pending_appointment_adaptor.pos).get(SERVICE_ID);
            Log.d(TAG, "comman_Stuff: " + strBookingId + " " + pending_appointment_adaptor.pos);
            new Json_Response(getActivity(), progressView, getHashmap_Values("delete")).call_Webservices(this, "deletebooking");
        } else if(s.equalsIgnoreCase("alertDialoge")){
            if (!verifyStoragePermissions()) {
                //  AppointmentHistory_Dialoge appointmentHistory_dialoge = new AppointmentHistory_Dialoge(getActivity());
                //  appointmentHistory_dialoge.dialogueGlovie_Image(list.get(pending_appointment_adaptor.pos).get(Pending_Appointment.JSON_ITEM));

                Intent intent = new Intent(getActivity(), Booking_Appointment_View.class);

                String strJson=list.get(pending_appointment_adaptor.pos).get(Pending_Appointment.JSON_ITEM);
                intent.putExtra(Appointment_Constant.JSON_BUNDLE, strJson);
                intent.putExtra(Appointment_Constant.MESSAGE, strMessage);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity(). startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                }
            }
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public boolean verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

            return true;
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "onRequestPermissionsResult: ");
                    Intent intent = new Intent(getActivity(), Booking_Appointment_View.class);

                    String strJson=list.get(pending_appointment_adaptor.pos).get(Pending_Appointment.JSON_ITEM);
                    intent.putExtra(Appointment_Constant.JSON_BUNDLE, strJson);
                    intent.putExtra(Appointment_Constant.MESSAGE, strMessage);
                    getActivity().startActivity(intent);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: accepted");

        if (isView){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressView.setVisibility(View.GONE);
                }
            },410);

        }
        else {
            Log.d(TAG, "onResume: isview accepted");
        }
        if (progressView.isShown()){
            Log.d(TAG, "onResume: shown accepted");
        }
    }

}

