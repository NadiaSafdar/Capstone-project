package com.afroexaentric.Login_SignUp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afroexaentric.Comman_Stuffs.Check_EditText;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Comman_Stuffs.isNetworkAvailable;
import com.afroexaentric.More_Options;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.afroexaentric.R;
import com.google.android.material.textfield.TextInputLayout;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONObject;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener, Json_Callback {
    private Button btnLogin;
    private TextView tvRegister;
    private GeometricProgressView progressView;
    private EditText etUserName,etPassword;
    private TextInputLayout ti_UserName,tiPassword;
    private JSONObject jsonObject;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        sharedPreferences=getSharedPreferences(Log_Constants.MY_PREFS,MODE_PRIVATE);
        //Intializing widgtes
        initWidgets();
    }

    //Intializing widgtes
    private void initWidgets() {
        progressView=(GeometricProgressView)findViewById(R.id.progressView);

        etUserName=(EditText)findViewById(R.id.et_loginUserID);
        etPassword=(EditText)findViewById(R.id.et_Password);

        ti_UserName=(TextInputLayout)findViewById(R.id.inputLayout_LoginUserID);
        tiPassword=(TextInputLayout)findViewById(R.id.inputLayout_LoginPass);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);

        ///click listner, Views click register
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
               login_User();
              //  startActivity(new Intent(Login_Activity.this,More_Options.class));
                break;
            case R.id.tvRegister:
                startActivity(new Intent(Login_Activity.this,Registeration_SignUp.class));
                break;
        }
    }

    private void login_User() {

        if (!new isNetworkAvailable(Login_Activity.this).isConnectivity()) {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        if (!new Check_EditText(Login_Activity.this).checkUserame(etUserName, ti_UserName, "message")) {
            return;
        }

        if (!new Check_EditText(Login_Activity.this).checkUserame(etPassword, tiPassword, "message")) {
            return;
        } else {
            progressView.setVisibility(View.VISIBLE);
            new Json_Response(Login_Activity.this, progressView, getHashmap_Values()).call_Webservices(this,"login");
        }
    }
    private HashMap<String, String> getHashmap_Values() {
        HashMap<String,String>map=new HashMap<>();
        map.put("email",etUserName.getText().toString());
        map.put("password",etPassword.getText().toString());
        //http://qrcode.iconsulting.com.pk/api/login?email=rurazza@gmail.com&password=abc
        return map;
    }

    @Override
    public void update_Response(JSONObject json) {

        int success;
        try {

            if (json != null) {
                success = json.getInt(Network_Stuffs.SUCCESS);
                if (success == 1) {
                    jsonObject=json.getJSONObject("user");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Login_Contstant.AUTH_TOKEN, json.getString("Token"));

                    editor.putString(Login_Contstant.EMAIL, jsonObject.getString(Login_Contstant.EMAIL));
                    editor.putString(Login_Contstant.FIRST_NAME, jsonObject.getString("name"));

                    editor.putString(Login_Contstant.USER_ID, jsonObject.getString("id"));
                    editor.putString(Login_Contstant.USER_KEY, jsonObject.getString("name"));

                    editor.putString(Login_Contstant.PHONE_NO, jsonObject.getString("phone"));

                    editor.putString(Login_Contstant.PASS_KEY, etPassword.getText().toString());

                    editor.putString(Login_Contstant.USER_IMAGE, Network_Stuffs.BASE_URL+jsonObject.getString("image"));
                    editor.commit();

                    Intent intent = new Intent(Login_Activity.this, More_Options.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                        finish();
                        return;
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "login error: " + e.getMessage());
        }
    }
}
