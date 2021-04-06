package com.afroexaentric.Gallery;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Comman_Stuffs.RecyclerItemClickListener;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Gallery_Collections extends AppCompatActivity implements Json_Callback {
    public static final String IMAGE_NAME = "img_name";
    public static final String IMAGE_ID = "imgId";
    public static final String IMAGE_URL = "image_url";


    private SharedPreferences sharedPreferences;

    private Gallery_Collections_Adaptor gallery_collections_adaptor;
    private RecyclerView recyclerView;

    private TextView tvNoData;

    private JSONObject jsonObject;
    private GeometricProgressView progressView;
    ArrayList<HashMap<String, String>> list;
    private String strVehicleInfoID = "";
    private String strWebData = "";
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_collections);

        sharedPreferences=getSharedPreferences(Log_Constants.MY_PREFS,MODE_PRIVATE);
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
        tvNoData = (TextView) findViewById(R.id.tvNo_Data);
        // set up the RecyclerView
        recyclerView = findViewById(R.id.rv);

        //arraylist
        list = new ArrayList<>();
        //no of coloum in gridview
        final int numberOfColumns = 2;
        /// recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfColumns));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), numberOfColumns);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (gallery_collections_adaptor.getItemViewType(position)) {
                    case Gallery_Collections_Adaptor.ONE_ROW:
                        return numberOfColumns;
                    case Gallery_Collections_Adaptor.TWO_ROW:
                        return 1;
                    default:
                        return -1;
                }
            }
        });

        recyclerView.setLayoutManager(gridLayoutManager);

        //recycler view item click
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void call_Webservices() {
        new Json_Response(Gallery_Collections.this, progressView, getHashmap_Values()).call_Webservices(this,"gallery");

    }

    ///sending params to webserver
    private HashMap<String, String> getHashmap_Values() {

        HashMap<String, String> hashMap = new HashMap<>();
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
                    set_Recyler_View();
                } else {
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
            JSONArray jsonArray = jsonObject.getJSONArray("gallery");
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject jsonItem = jsonArray.getJSONObject(a);
                HashMap<String, String> map = new HashMap<>();

                map.put(IMAGE_NAME, jsonItem.getString("name"));
                map.put(IMAGE_URL, jsonItem.getString("image"));
                map.put(IMAGE_ID, jsonItem.getString("id"));

                list.add(map);
            }
            //set recycler view ,adaptor, animation
            //set adaptor
            //set adaptor
            gallery_collections_adaptor = new Gallery_Collections_Adaptor(Gallery_Collections.this, list);
            recyclerView.setAdapter(gallery_collections_adaptor);
        } catch (Exception e) {
            Log.d(TAG, "set_Recyler_View: " + e.getMessage());
        }

    }
}





