package com.afroexaentric.Reviews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.afroexaentric.Comman_Stuffs.Check_EditText;
import com.afroexaentric.Comman_Stuffs.Keyboard_Close;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.LogCalls.LogCalls_Debug;
import com.afroexaentric.Login_SignUp.Login_Contstant;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.afroexaentric.R;
import com.google.android.material.textfield.TextInputLayout;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Review_Service extends AppCompatActivity implements Json_Callback, View.OnClickListener {

    public static final String NAME = "name";
    public static final String REVIEWS = "reviews";
    public static final String RATING = "rating";
    public static final String IMAGE_URL = "img_url";

    private Review_Service_Adaptor reviewServiceAdaptor;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String, String>> list;

    private LinearLayout layoutReviews;
    private Button btnReview;
    private EditText etReview;
    private TextInputLayout tiReview;

    private RatingBar ratingBar;

    private JSONObject jsonObject;
    private SharedPreferences sharedPreferences;

    private GeometricProgressView progressView;
    private String strWebservice = "";
    private String strBookingId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.review_service);

        sharedPreferences = getSharedPreferences(Log_Constants.MY_PREFS, MODE_PRIVATE);
        //Toolbar initialization and setup
        toolbar_init();
        ///Intializing widgtes
        initWidgets();
        ///call webservices
        call_Webservices("allreview");

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

        layoutReviews = (LinearLayout) findViewById(R.id.layout_Reviews);
        etReview = (EditText) findViewById(R.id.et_Reviews);
        tiReview = (TextInputLayout) findViewById(R.id.ti_Reviews);
        btnReview = (Button) findViewById(R.id.btnReviews);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        //arraylist
        list = new ArrayList<>();

        ///call adaptor
        reviewServiceAdaptor = new Review_Service_Adaptor(Review_Service.this, list);
        recyclerView.setAdapter(reviewServiceAdaptor);
        ///click event called
        btnReview.setOnClickListener(this);
    }

    ///click event callback method
    @Override
    public void onClick(View view) {
        if (!new Check_EditText(Review_Service.this).checkUserame(etReview, tiReview, "error")) {
            return;
        }
        ///For hide keyboard
        new Keyboard_Close(Review_Service.this).keyboard_Close_Down();

        progressView.setVisibility(View.VISIBLE);
        //call webservices
        call_Webservices("review");
    }

    ///call webservices
    private void call_Webservices(String strReview) {
        new Json_Response(Review_Service.this, progressView, getHashmap_Values(strReview)).call_Webservices(this, strReview);
    }

    ///sending params to webserver
    private HashMap<String, String> getHashmap_Values(String strReview) {

        strWebservice = strReview;
        HashMap<String, String> hashMap = new HashMap<>();
        if (strWebservice.equalsIgnoreCase("review")) {
            hashMap.put("rating", String.valueOf(ratingBar.getRating()));
            hashMap.put("comments", etReview.getText().toString());
            hashMap.put("booking_id", strBookingId);
        }

        hashMap.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, ""));
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

                    set_Recyler_View_Data();
                    reviewServiceAdaptor.notifyDataSetChanged();
                    post_Review();
                    etReview.setText("");

                } else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                }
            }
        } catch (Exception e) {
            Log.d(TAG, "update_Response: " + e.getMessage());
        }
    }

    private void post_Review() {
        try {

            if (jsonObject.getString("is_review_post").equalsIgnoreCase("1")) {
                strBookingId=jsonObject.getJSONObject("booking").getString("id");
                layoutReviews.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void set_Recyler_View_Data() {

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("reviews");
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject jsonItem = jsonArray.getJSONObject(a);
                HashMap<String, String> map = new HashMap<>();

                map.put(NAME, jsonItem.getString("name"));
                map.put(RATING, jsonItem.getString("rating"));
                map.put(IMAGE_URL, Network_Stuffs.BASE_URL + jsonItem.getString("userimage"));
                map.put(REVIEWS, jsonItem.getString("comments"));

                list.add(a, map);
            }


//            if (list.size() > 0) {
//                schedule_services_adaptor.notifyDataSetChanged();
//                // rvServicePlan.setAdapter(valueMy_trade_adaptor);
//                // tvNo_ServicePlan.setVisibility(View.GONE);
//            } else {
//                //tvNo_ServicePlan.setVisibility(View.VISIBLE);
//
//            }
        } catch (Exception e) {
            LogCalls_Debug.d(TAG, "set_Recyler_View: " + e.getMessage());
        }
    }


}