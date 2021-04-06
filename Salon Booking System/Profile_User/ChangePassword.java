package com.afroexaentric.Profile_User;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afroexaentric.Comman_Stuffs.Check_EditText;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Comman_Stuffs.isNetworkAvailable;
import com.afroexaentric.LogCalls.LogCalls_Debug;
import com.afroexaentric.Login_SignUp.Login_Contstant;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.afroexaentric.R;
import com.google.android.material.textfield.TextInputLayout;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONObject;

import java.util.HashMap;

import androidx.appcompat.app.AlertDialog;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class ChangePassword implements Json_Callback {

    private Activity activity;
    private EditText etOld_Pass,etPass,etConfirm_Pass;
    private GeometricProgressView progressView;
    private TextInputLayout tiOld_Pass,tiAlert_Pass,tiAlert_ConfirmPass;
    private Json_Callback json_callback;
    private Button btnSubmit;
    private AlertDialog alertDialog;
    private static final int LONG_DELAY = 3500; // 3.5 seconds
    private SharedPreferences sharedPreferences;

    public ChangePassword(Activity login_new) {
        activity = login_new;
        json_callback = this;
        sharedPreferences=activity.getSharedPreferences(Log_Constants.MY_PREFS,Context.MODE_PRIVATE);
    }

    public void alert_ForgotPass() {

        alertDialog = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle).create();
        if (!alertDialog.isShowing()) {

            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.change_password, null);
            alertDialog.setView(view);
            //  alertDialog.setTitle("Forgot Password");
            // alertDialog.setIcon(R.drawable.ico_user);
            alertDialog.setCancelable(false);
            //  alertDialog.setCanceledOnTouchOutside(false);
            View titleView = inflater.inflate(R.layout.dialogue_titleview, null);
            alertDialog.setCustomTitle(titleView);
            TextView tv = (TextView) titleView.findViewById(R.id.tvDialogueTitle);
            tv.setText("Change Password");

            progressView = (GeometricProgressView) view.findViewById(R.id.progressView);

            etOld_Pass = (EditText) view.findViewById(R.id.et_Old_Pass);
            etPass = (EditText) view.findViewById(R.id.et_Pass);
            etConfirm_Pass = (EditText) view.findViewById(R.id.et_ConfirmPassword);

            tiOld_Pass = (TextInputLayout) view.findViewById(R.id.inputLayout_AlertOldPass);
            tiAlert_Pass = (TextInputLayout) view.findViewById(R.id.inputLayout_AlertPass);
            tiAlert_ConfirmPass = (TextInputLayout) view.findViewById(R.id.inputLayout_ConfirmPass);

            btnSubmit = (Button) view.findViewById(R.id.btn_Submit_Forgot);
            Button btncancel = (Button) view.findViewById(R.id.btnCancel_Alert);



            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!new isNetworkAvailable(activity).isConnectivity()) {
                        Toast.makeText(activity, "No internet connection", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!new Check_EditText(activity).checkUserame(etOld_Pass, tiOld_Pass, "message")) {
                        return;
                    }
                    if (!new Check_EditText(activity).checkUserame(etPass, tiAlert_Pass, "message")) {
                        return;
                    }
                    if (!new Check_EditText(activity).checkUserame(etConfirm_Pass, tiAlert_ConfirmPass, "message")) {
                        return;
                    }


                    if (!(etOld_Pass.getText().toString().equals("")&&etPass.getText().toString().equals("")&&etConfirm_Pass.getText().toString().equals(""))) {
                        if (etOld_Pass.getText().toString().equals(sharedPreferences.getString(Login_Contstant.PASS_KEY, null))) {
                            if (!etOld_Pass.getText().toString().equals(etPass.getText().toString())) {
//                            if (!checkPasswordEmpty()) {
//                                return false;
//                            }
                                if (etPass.getText().length() < 5) {
                                    Toast.makeText(activity, "Password must be greater then 6 digits", Toast.LENGTH_LONG).show();
                                    return ;

                                }

                                if (!etPass.getText().toString().equals(etConfirm_Pass.getText().toString())) {
                                    Toast.makeText(activity, "Confirm password is incorrect", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            } else {
                                Toast.makeText(activity, "Old and new password cannot be same", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } else {
                            Toast.makeText(activity, "Type correct old password", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                   // else {

                        InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(etPass.getWindowToken(), 0);
                        //  forgot_Password(alertDialog);
                        //calling webservices
                        //getting jsonobject in return via jsoncallback interface
                        progressView.setVisibility(View.VISIBLE);
                        new Json_Response(activity, btnSubmit, getHashmap_Values("password")).call_Webservices(json_callback, "changepassword");
                  //  }
                    //  alertDialog.dismiss();
                }
            });
            btncancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialog.dismiss();

                }
            });
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
            alertDialog.show();
            //  widgets_Init();
        }

    }

    private HashMap<String, String> getHashmap_Values(String data) {
        String strPass = etOld_Pass.getText().toString();
        String strNewPass = etPass.getText().toString();
        String strConfirmPass = etConfirm_Pass.getText().toString();

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("currentPassword", strPass);
        hashMap.put("newPassword", strNewPass);
        hashMap.put("confirmPassword", strConfirmPass);

        hashMap.put("id", sharedPreferences.getString(Login_Contstant.USER_ID, ""));
        hashMap.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
        return hashMap;
    }

    @Override
    public void update_Response(JSONObject jsonObject) {
        progressView.setVisibility(View.GONE);
        alertDialog.dismiss();

        try {

            if (jsonObject != null) {

                String success = jsonObject.getString(Network_Stuffs.SUCCESS);
                if (success.equalsIgnoreCase("1")) {
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(Login_Contstant.PASS_KEY,etPass.getText().toString()).commit();
                    //show toast
                    show_Toast("Password is changed" );
                    // show_Toast("" + jsonObject.getString("message"));

                } else {

                    String strMsg = "Username and password is mismatched. Please try again with correct username and password.";
                    //show toast
                    show_Toast(""+jsonObject.getString("message"));
                }
            }
        } catch (Exception e) {
            alertDialog.dismiss();

            LogCalls_Debug.d(TAG, "update_Response: " + e.getMessage());
        }

    }

    private void show_Toast(String strMsg) {

        final Toast toast = Toast.makeText(activity, strMsg, Toast.LENGTH_LONG);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 4500);

    }

}


