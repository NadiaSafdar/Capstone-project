package com.afroexaentric;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Login_SignUp.Login_Contstant;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONObject;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class About_Us extends AppCompatActivity implements  Json_Callback {
    //OnMapReadyCallback,

    private Toolbar toolbar;
    private Marker marker;
    private GoogleMap mMap;
    private GeometricProgressView progressView;
    private ImageView imgAbout;
    TextView tvName, tvReviews, tvDesc, tvMon, tvTuesday, tvWed, tvThursday, tvFri, tvSat, tvSun;
    private RatingBar ratingBar;

    private JSONObject jsonObject = null;
    private float lat=33.5639389f;
    private float lang=73.0217122f;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        sharedPreferences=getSharedPreferences(Log_Constants.MY_PREFS,MODE_PRIVATE);
        //Toolbar initialization and setup
        toolbar_init();
        initWidgets();
      // set_Map();
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
        imgAbout = (ImageView) findViewById(R.id.img);

        ratingBar=(RatingBar)findViewById(R.id.ratingBar);

        tvName=(TextView)findViewById(R.id.tvName);
        tvDesc=(TextView)findViewById(R.id.tvDesc);
        tvReviews=(TextView)findViewById(R.id.tv_Reviews);

        tvMon = (TextView) findViewById(R.id.tvMonday);
        tvTuesday = (TextView) findViewById(R.id.tvTuesday);
        tvWed = (TextView) findViewById(R.id.tvWed);
        tvThursday = (TextView) findViewById(R.id.tvThursday);
        tvFri = (TextView) findViewById(R.id.tvFri);
        tvSat = (TextView) findViewById(R.id.tvSat);
        tvSun = (TextView) findViewById(R.id.tvSun);
    }


    private void call_Webservices() {
        progressView.setVisibility(View.VISIBLE);
        new Json_Response(About_Us.this, progressView, getHashmap_Values()).call_Webservices(this, "about");

    }

    ///sending params to webserver
    private HashMap<String, String> getHashmap_Values() {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
        return hashMap;
    }

    @Override
    public void update_Response(JSONObject json) {
        jsonObject = json;
        int success;
        try {


            success = json.getInt(Network_Stuffs.SUCCESS);
            if (success == 1) {
                tvReviews.setText(String.format("%s Reviews", jsonObject.getString("totalreview")));
                jsonObject = json.getJSONObject("about");
                setData();
            }
        } catch (Exception e) {
            Log.d(TAG, "update_Response: " + e.getMessage());
        }
    }

    private void setData() {
        try {
            lat= Float.parseFloat(jsonObject.getString("lat"));
            lang= Float.parseFloat(jsonObject.getString("lng"));
            /////// place marker on map getting current location lat and long coordinates
           // markerOnMap();

            Picasso.with(About_Us.this).load(jsonObject.getString("image")).placeholder(R.drawable.logo).into(imgAbout);

            ratingBar.setRating(jsonObject.getInt("rating"));

            tvName.setText(jsonObject.getString("name"));
            tvDesc.setText(jsonObject.getString("description"));

            tvMon.setText(jsonObject.getString("monday_from")+" - "+jsonObject.getString("monday_to"));
            tvTuesday.setText(jsonObject.getString("tuesday_from")+" - "+jsonObject.getString("tuesday_to"));
            tvWed.setText(jsonObject.getString("wednesday_from")+" - "+jsonObject.getString("wednesday_to"));
            tvThursday.setText(jsonObject.getString("thursday_from")+" - "+jsonObject.getString("thursday_to"));
            tvFri.setText(jsonObject.getString("friday_from")+" - "+jsonObject.getString("friday_to"));
            tvSat.setText(jsonObject.getString("saturday_from")+" - "+jsonObject.getString("saturday_to"));
            tvSun.setText(jsonObject.getString("sunday_from")+" - "+jsonObject.getString("sunday_to"));


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "setData: error"+e.getMessage());
        }
    }
}

//    private void set_Map() {
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map_Shop);
//        mapFragment.getMapAsync(this);
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        /////// place marker on map getting current location lat and long coordinates
//        markerOnMap();
//    }
//    /////// place marker on map getting current location lat and long coordinates
//    private void markerOnMap() {
//        try {
//
//            LatLng latLng = new LatLng(lat, lang);
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng)
//                    .title(getResources().getString(R.string.app_name))
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//            marker = mMap.addMarker(markerOptions);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
//
//        } catch (Exception e) {
//            Log.d("json Error", e.getMessage());
//        }
//    }