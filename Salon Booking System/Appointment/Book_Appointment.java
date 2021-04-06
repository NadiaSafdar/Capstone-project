package com.afroexaentric.Appointment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afroexaentric.Appointment.AppointmentHistory.Booking_Appointment_Data;
import com.afroexaentric.Comman_Stuffs.Check_EditText;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Login_SignUp.Login_Contstant;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.R;
import com.google.android.material.textfield.TextInputLayout;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONObject;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Book_Appointment extends AppCompatActivity implements View.OnClickListener, Json_Callback {

    private SharedPreferences sharedPreferences;
    private TextView tvDate;
    private EditText etUser,etPhone,et_Email;
    private TextInputLayout tiUser,tiPhone,tiEmail;
    private Button btnBook_Appointment;
    private GeometricProgressView progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_appointment);

        sharedPreferences = getSharedPreferences(Log_Constants.MY_PREFS, MODE_PRIVATE);
        ///Intializing widgtes
        initWidgets();
    }

    ///Intializing widgtes
    private void initWidgets() {
        tvDate = (TextView) findViewById(R.id.tvDate_Picker);

        btnBook_Appointment=(Button)findViewById(R.id.btn);
        etUser=(EditText)findViewById(R.id.et_loginUserID);
        etPhone=(EditText)findViewById(R.id.et_PhoneNo);
        et_Email=(EditText)findViewById(R.id.et_Email);

        tiUser=(TextInputLayout)findViewById(R.id.inputLayout_LoginUserID);
        tiPhone=(TextInputLayout)findViewById(R.id.inputLayout_PhoneNo);
        tiEmail=(TextInputLayout)findViewById(R.id.inputLayout_Email);

        btnBook_Appointment.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(Book_Appointment.this, Booking_Appointment_Data.class));
        if (!checkData_Exist()){
            return;
        }
        ///call webservices
        progressView.setVisibility(View.VISIBLE);
        new Json_Response(Book_Appointment.this, progressView, getHashmap_Values()).call_Webservices(this,"signup");

    }

    private boolean checkData_Exist() {
        try {

            if (!new Check_EditText(this).checkUserame(etUser, tiUser, "message")) {
                return false;
            }
            if (!new Check_EditText(this).checkUserame(et_Email, tiEmail, "message")) {
                return false;
            }
            if (!new Check_EditText(this).checkUserame(etPhone, tiPhone, "message")) {
                return false;
            }
            if (!new Check_EditText(Book_Appointment.this).isValidEmail(et_Email.getText().toString())) {
                Toast.makeText(this, "Please type correct email", Toast.LENGTH_LONG).show();
                return false;
            }
            if (etPhone.getText().toString().replace("(", "").replace(")", "").replace("-", "").replace(" ", "").length() != 10) {
                Toast.makeText(this, "Please type correct phone number", Toast.LENGTH_LONG).show();
                return false;
            }
            if (!new Check_EditText(Book_Appointment.this).isValidPhoneNumber(etPhone.getText().toString())) {
                Toast.makeText(this, "Please type correct phone number", Toast.LENGTH_LONG).show();
                return false;
            }


        } catch (Exception e) {
            Log.d(TAG, "checkData_Exist: " + e.getMessage());
        }
        return true;
    }

    private HashMap<String, String> getHashmap_Values() {
        HashMap<String,String>map=new HashMap<>();
        map.put("name",etUser.getText().toString());
        map.put("phone",etPhone.getText().toString());
        map.put("email",et_Email.getText().toString());
        map.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
        return map;
    }

    @Override
    public void update_Response(JSONObject jsonObject) {

    }
}