package com.afroexaentric.Profile_User;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afroexaentric.Activity_Transhion_Enter_Exit.Activity_Transition;
import com.afroexaentric.Appointment.AppointmentHistory.Booking_Appointment_Data;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Login_SignUp.Landing_Activity;
import com.afroexaentric.Login_SignUp.Login_Contstant;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Stuff extends AppCompatActivity implements View.OnClickListener, Json_Callback {
    private Button btnUpdate_Profile,btnBookingHistory,btnMemberShip,btnUser_Our_Office,btnLogout;
    private CircleImageView imgUser;
    private TextView tvUserName;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity enter/exit transition class
        //animating content layout when entering or leaving the screen
        new Activity_Transition(Profile_Stuff.this).transition_Acivity();
        setContentView(R.layout.profile_stuff);

        sharedPreferences=getSharedPreferences(Log_Constants.MY_PREFS,MODE_PRIVATE);
        //Toolbar initialization and setup
        toolbar_init();
        ///Intializing widgtes
        initWidgets();

        setPrefsData();

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
        imgUser=(CircleImageView)findViewById(R.id.imgUser);
        tvUserName=(TextView)findViewById(R.id.tv_UserName);
        btnUpdate_Profile=(Button)findViewById(R.id.btnUpdate_Profile);
        btnBookingHistory=(Button)findViewById(R.id.btnBooking_History);
        btnMemberShip=(Button)findViewById(R.id.btnMemberShip);
        btnUser_Our_Office=(Button)findViewById(R.id.btnUse_Office);
        btnLogout=(Button)findViewById(R.id.btnLogout);

        ///click listener
        btnUpdate_Profile.setOnClickListener(this);
        btnBookingHistory.setOnClickListener(this);
        btnMemberShip.setOnClickListener(this);
        btnUser_Our_Office.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }
    private void setPrefsData() {

        Picasso.with(getApplicationContext()).load(sharedPreferences.getString(Login_Contstant.USER_IMAGE, "http://")).placeholder(R.color.gray).into(imgUser);
        tvUserName.setText(sharedPreferences.getString(Login_Contstant.FIRST_NAME,""));

    }
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btnUpdate_Profile:

                 intent=new Intent(Profile_Stuff.this,User_Profile.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this,imgUser,"abc").toBundle());
                }
                break;
            case R.id.btnLogout:
                logout();
                break;
            case R.id.btnBooking_History:
                 intent=new Intent(Profile_Stuff.this, Booking_Appointment_Data.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }
                //startActivity(new Intent(Profile_Stuff.this, Booking_Appointment_Data.class));
            break;

        }
    }

    //////////////////////////////////***************************************////////////////////////////////////////////////////////////////*************
    public void logout() {
        new Json_Response(Profile_Stuff.this, btnUpdate_Profile, getHashmap_Values("msg")).call_Webservices(this,
                "logout");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(Profile_Stuff.this, Landing_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        //finish();
    }

    private HashMap<String, String> getHashmap_Values(String msg) {
      HashMap<String,String> map = new HashMap<>();
        map.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, ""));
        map.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
        return map;
    }

    @Override
    public void update_Response(JSONObject jsonObject) {

    }
}