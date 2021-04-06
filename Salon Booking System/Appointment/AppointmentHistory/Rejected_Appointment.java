package com.afroexaentric.Appointment.AppointmentHistory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Rejected_Appointment extends Fragment implements Json_Callback,Comman_Stuff_Interface {
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.rejected_appointment, container, false);
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
        progressView = (GeometricProgressView)view. findViewById(R.id.progressView);
        tvNo_Data = (TextView) view.findViewById(R.id.tvNo_Data);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        //arraylist
        list = new ArrayList<>();
    }


    ///call webservices
    private void call_Webservices() {
        new Json_Response(getActivity(), progressView, getHashmap_Values()).call_Webservices(this, "allbooking");
    }

    ///sending params to webserver
    private HashMap<String, String> getHashmap_Values() {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, ""));
        hashMap.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
        hashMap.put("status", "3");
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
                    //set recyclerview data and call adaptor
                    set_Recyler_View();
                    tvNo_Data.setText(jsonObject.getString("message"));

                } else {
                   // Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    tvNo_Data.setVisibility(View.VISIBLE);
                    tvNo_Data.setText(jsonObject.getString("message"));
                }
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

                JSONArray jsonArray = jsonItem.getJSONArray("subservices");

                HashMap<String, String> map = new HashMap<>();

                map.put(SERVICE_NAME, jsonArray.getJSONObject(0).getString("name"));
                map.put(DESCRIPTION, jsonArray.getJSONObject(0).getString("description"));
                map.put(IMAGE_URL, Network_Stuffs.BASE_URL + jsonArray.getJSONObject(0).getString("image"));
                map.put(PRICE, jsonItem.getString("total"));
                map.put(SERVICE_ID, jsonItem.getString("id"));
                map.put(Pending_Appointment.TIME_AGO, strTime);
                map.put(Pending_Appointment.DATE, strRequestedDate);
                map.put(Appointment_Constant.PAYMENT_ID,jsonItem.getString(Appointment_Constant.PAYMENT_ID));
                map.put(Pending_Appointment.JSON_ITEM, jsonItem.toString());
                map.put(Pending_Appointment.STATUS, "3");
                map.put(ADDCART_VALUE, "1");
                list.add(map);
                // }
            }

            ///call adaptor
            pending_appointment_adaptor = new Pending_Appointment_Adaptor(getActivity(), list,this);
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
          //  strBookingId = list.get(pending_appointment_adaptor.pos).get(SERVICE_ID);
          //  new Json_Response(getActivity(), progressView, getHashmap_Values("delete")).call_Webservices(this, "deletebooking");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: rejected");

        if (isView){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressView.setVisibility(View.GONE);
                }
            },410);

        }
        else {
            Log.d(TAG, "onResume: isview rejected");
        }
        if (progressView.isShown()){
            Log.d(TAG, "onResume: shown rejected");
        }
    }
}

