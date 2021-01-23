package com.afroexaentric.Our_Services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Comman_Stuffs.RecyclerItemClickListener;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.afroexaentric.R;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;


public class Our_Services extends AppCompatActivity implements Json_Callback {
    public static final String SERVICE_NAME = "service_name";
    public static final String IMAGE_URL = "image_url";
    public static final String SERVICE_ID = "serviceID";

    private SharedPreferences sharedPreferences;

    private Our_Services_Adaptor our_services_adaptor;
    private RecyclerView recyclerView;

    private TextView tvNoData;

    private JSONObject jsonObject;
    private GeometricProgressView progressView;
    ArrayList<HashMap<String, String>> list;
    private String strVehicleInfoID="";
    private String strWebData="";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.our__services);

        sharedPreferences = getSharedPreferences(Log_Constants.MY_PREFS, MODE_PRIVATE);
        //Toolbar initialization and setup
        toolbar_init();
        ///Intializing widgtes
        initWidgets();
        ///call webservices
        call_Webservices();
    }

    //Toolbar initialization and setup
    private void toolbar_init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

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

    //Intializing widgtes
    private void initWidgets() {
        progressView = (GeometricProgressView) findViewById(R.id.progressView);
        tvNoData=(TextView)findViewById(R.id.tvNo_Data);
        // set up the RecyclerView
        recyclerView = findViewById(R.id.rv);

        //no of coloum in gridview
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfColumns));

        //arraylist
        list=new ArrayList<>();
        //recycler view item click
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(View view, int position) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(SERVICE_ID,list.get(position).get(SERVICE_ID)).commit();
                startActivity(new Intent(Our_Services.this, Sub_Service.class));
                //startActivity(new Intent(Our_Services.this, Booking_Appointment_Data.class));
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
    }

    private void call_Webservices() {
        our_services_adaptor = new Our_Services_Adaptor(Our_Services.this, list);
        recyclerView.setAdapter(our_services_adaptor);

        new Json_Response(Our_Services.this, progressView, getHashmap_Values("data")).call_Webservices(this,"services");
    }

    ///sending params to webserver
    private HashMap<String, String> getHashmap_Values(String strData) {
        strWebData=strData;

        HashMap<String, String> hashMap = new HashMap<>();
       //hashMap.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
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
                    set_Recyler_View();
                }
                else {
                    Toast.makeText(Our_Services.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText(jsonObject.getString("message"));
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "update_Response: " + e.getMessage());
        }
    }

    //set recycler view ,adaptor, animation
    public void set_Recyler_View() {
        Log.d(TAG, "set_Recyler_View: ");

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("services");
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject jsonItem = jsonArray.getJSONObject(a);
                HashMap<String, String> map = new HashMap<>();

                map.put(SERVICE_NAME, jsonItem.getString("name"));
                map.put(IMAGE_URL, jsonItem.getString("image"));
                map.put(SERVICE_ID, jsonItem.getString("id"));

                list.add(map);
            }
            //set adaptor
            our_services_adaptor = new Our_Services_Adaptor(Our_Services.this, list);
            recyclerView.setAdapter(our_services_adaptor);
        } catch (Exception e) {
            Log.d(TAG, "set_Recyler_View: " + e.getMessage());
        }

    }
}





