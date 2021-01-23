package com.afroexaentric.Our_Services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afroexaentric.Appointment.AppointmentHistory.Pending_Appointment;
import com.afroexaentric.Comman_Stuffs.Gson_List_Prefs;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Sub_Service extends AppCompatActivity implements Json_Callback, View.OnClickListener {

    public static final String SERVICE_NAME = "service_name";
    public static final String IMAGE_URL = "image_url";
    public static final String SERVICE_ID = "serviceID";
    public static final String PRICE = "service_price";
    public static final String TIME = "service_time";
    public static final String DURATION_TYPE = "duration_type";
    public static final String DESCRIPTION = "service_description";
    public static final String ADDCART_VALUE = "1";


    private Sub_Service_Adaptor sub_service_adaptor;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String, String>> list;
    private TextView tvNo_Data;

    private Button btnNext;
    private JSONObject jsonObject;
    private SharedPreferences sharedPreferences;

    private GeometricProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sub_service);

        sharedPreferences = getSharedPreferences(Log_Constants.MY_PREFS, MODE_PRIVATE);

        //remove addcart array after buying
        new Gson_List_Prefs(Sub_Service.this).removeArray_List();
        //Toolbar initialization and setup
        toolbar_init();
        ///Intializing widgtes
        initWidgets();
        ///call webservices
        call_Webservices();

    }


    //Toolbar initialization and setup
    private void toolbar_init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // / add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    ///Intializing widgtes
    private void initWidgets() {
        progressView = (GeometricProgressView) findViewById(R.id.progressView);
        tvNo_Data = (TextView) findViewById(R.id.tvNo_Data);

        btnNext = (Button) findViewById(R.id.btnNext);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        //arraylist
        list = new ArrayList<>();
        ///click event called
        btnNext.setOnClickListener(this);

        //recycler view item click
//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onItemClick(View view, int position) {
//                //save or remove item from cart list
//                new Gson_List_Prefs(Sub_Service.this).saveItem(list.get(position));
//                startActivity(new Intent(Sub_Service.this, Service_Add_Data.class));
//                //startActivity(new Intent(Our_Services.this, Booking_Appointment_Data.class));
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //remove addcart array after buying
        new Gson_List_Prefs(Sub_Service.this).removeArray_List();
        try {
            if (sub_service_adaptor != null)
                sub_service_adaptor.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///click event callback method
    @Override
    public void onClick(View view) {
        ArrayList<HashMap<String, String>> list = new Gson_List_Prefs(Sub_Service.this).getShopinglist();
        if (list.size() != 0)
            startActivity(new Intent(Sub_Service.this, Service_Add_Data.class));
        else
            Toast.makeText(getApplicationContext(), "At least one service should be in cart to proceed booking appointment", Toast.LENGTH_SHORT).show();
    }

    ///call webservices
    private void call_Webservices() {
        new Json_Response(Sub_Service.this, progressView, getHashmap_Values()).call_Webservices(this, "subservices");
    }

    ///sending params to webserver
    private HashMap<String, String> getHashmap_Values() {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("service_id", sharedPreferences.getString(Our_Services.SERVICE_ID, ""));
        hashMap.put("os", "android");
        hashMap.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
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
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
            JSONArray jsonArray = jsonObject.getJSONArray("subservices");
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject jsonItem = jsonArray.getJSONObject(a);
                HashMap<String, String> map = new HashMap<>();

                map.put(SERVICE_NAME, jsonItem.getString("name"));
                map.put(DESCRIPTION, jsonItem.getString("description"));
                map.put(IMAGE_URL, jsonItem.getString("image"));
                map.put(SERVICE_ID, jsonItem.getString("id"));
                map.put(PRICE, jsonItem.getString("price"));
                map.put(TIME, jsonItem.getString("duration"));
                map.put(DURATION_TYPE, jsonItem.getString("duration_type"));
                map.put(Pending_Appointment.JSON_ITEM, jsonItem.toString());
                map.put(ADDCART_VALUE, "1");
                list.add(a, map);
            }

            ///call adaptor
            sub_service_adaptor = new Sub_Service_Adaptor(Sub_Service.this, list);
            recyclerView.setAdapter(sub_service_adaptor);
            if (list.size() > 0) {
                 tvNo_Data.setVisibility(View.GONE);
            } else {
                tvNo_Data.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            LogCalls_Debug.d(TAG, "set_Recyler_View: " + e.getMessage());
        }
    }
}