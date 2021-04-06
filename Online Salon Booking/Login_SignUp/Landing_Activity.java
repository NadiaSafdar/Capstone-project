package com.afroexaentric.Login_SignUp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.More_Options;
import com.afroexaentric.R;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Landing_Activity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin, btnRegister;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(Log_Constants.MY_PREFS, MODE_PRIVATE);

        if (sharedPreferences.contains(Login_Contstant.USER_KEY)) {
            if (sharedPreferences.contains(Login_Contstant.PASS_KEY)) {
                intentDrawer();
                return;
            }
        }
        setContentView(R.layout.landing_activity);
        //Intializing widgtes
        initWidgets();
    }

    private void intentDrawer() {
        try {
            Intent intent = new Intent(Landing_Activity.this, More_Options.class);
            //make transition whiel navigation in activities
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

            } else {
                startActivity(intent);
            }
            finish();

        } catch (Exception e) {
            Log.d(TAG, "intentDrawer: " + e.getMessage());
        }
    }

    //Intializing widgtes
    private void initWidgets() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        ///click listner, Views click register
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                startActivity(new Intent(Landing_Activity.this, Login_Activity.class));
                break;
            case R.id.btnRegister:
                startActivity(new Intent(Landing_Activity.this, Registeration_SignUp.class));
                break;
        }
    }
}
