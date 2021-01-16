package com.afroexaentric.Login_SignUp;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afroexaentric.Comman_Stuffs.Check_EditText;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.More_Options;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.afroexaentric.Profile_User.Compress_Image;
import com.afroexaentric.R;
import com.google.android.material.textfield.TextInputLayout;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONObject;
import java.io.File;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Registeration_SignUp extends AppCompatActivity implements View.OnClickListener, Json_Callback {
    private Button btnRegister;
    private TextView tvRegister;

    private EditText etUser,etPhone,et_Email,etPassword,etConfirmPass;
    private TextInputLayout tiUser,tiPhone,tiEmail,tiPassword,tiConfirm_Pasword;
    private GeometricProgressView progressView;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 121;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int REQUEST_CAMERA = 323;
    private int CAPTURE_CAMERA = 1;
    private int gallery_Image = 2;

    private String imgPath;
    private File file;

    private Bitmap bitmap;
    private CircleImageView imgUser;
    private ImageView imgTaken;
    private String strMedia;
    private JSONObject jsonObject=null;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeration_sign_up);

        sharedPreferences=getSharedPreferences(Log_Constants.MY_PREFS,MODE_PRIVATE);
        //Intializing widgtes
        initWidgets();
    }

    //Intializing widgtes
    private void initWidgets() {
        progressView=(GeometricProgressView)findViewById(R.id.progressView);

        imgUser = (CircleImageView) findViewById(R.id.imgUser);
        imgTaken = (ImageView) findViewById(R.id.imgTaken);

        etUser=(EditText)findViewById(R.id.et_loginUserID);
        etPhone=(EditText)findViewById(R.id.et_PhoneNo);
        et_Email=(EditText)findViewById(R.id.et_Email);
        etPassword=(EditText)findViewById(R.id.et_Password);
        etConfirmPass=(EditText)findViewById(R.id.et_Confirm_Password);

        tiUser=(TextInputLayout)findViewById(R.id.inputLayout_LoginUserID);
        tiPhone=(TextInputLayout)findViewById(R.id.inputLayout_PhoneNo);
        tiEmail=(TextInputLayout)findViewById(R.id.inputLayout_Email);
        tiPassword=(TextInputLayout)findViewById(R.id.inputLayout_LoginPass);
        tiConfirm_Pasword=(TextInputLayout)findViewById(R.id.inputLayout_Login_ConfirmPass);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        tvRegister = (TextView) findViewById(R.id.tvRegister);

        ////////For phone format
        etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        ///click listner, Views click register
        btnRegister.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        imgTaken.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                if(!checkData_Exist()){
                    return;
                }
                ///call webservices
                progressView.setVisibility(View.VISIBLE);
                // new Json_Response(Registeration_SignUp.this, progressView, getHashmap_Values()).call_Webservices(this,"signup");
                new Json_Response(Registeration_SignUp.this, progressView, getHashmap_Values()).multipart_Volley(this, bitmap,"signup");

                break;
            case R.id.tvRegister:
                finish();
                break;
            case R.id.imgTaken:
                ///Select camera or gallery app for images uploadin
                getImageFrom_Gallery();
                break;
        }
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
            if (!new Check_EditText(this).checkUserame(etPassword, tiPassword, "message")) {
                return false;
            }
            if (!new Check_EditText(this).checkUserame(etConfirmPass, tiConfirm_Pasword, "message")) {
                return false;
            }

            if (!new Check_EditText(Registeration_SignUp.this).isValidEmail(et_Email.getText().toString())) {
                Toast.makeText(this, "Please type correct email", Toast.LENGTH_LONG).show();
                return false;
            }
//            if (etPhone.getText().toString().replace("(", "")
//                    .replace(")", "")
//                    .replace("-", "")
//                    .replace(" ", "").length() != 10) {
//                Log.d(TAG, "checkData_Exist: "+etPhone.length());
//               // Toast.makeText(this, "Please type correct phone number", Toast.LENGTH_LONG).show();
//                return false;
//            }
            if (!new Check_EditText(Registeration_SignUp.this).isValidPhoneNumber(etPhone.getText().toString())) {
                Toast.makeText(this, "Please type correct phone number", Toast.LENGTH_LONG).show();
                return false;
            }
            if (etPassword.getText().length() < 5) {
                Toast.makeText(getApplicationContext(), "Password must be greater then 6 digits", Toast.LENGTH_LONG).show();
                return false;

            }
            if (!etPassword.getText().toString().equals(etConfirmPass.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Confirm password is incorrect", Toast.LENGTH_LONG).show();
                return false;
            }
            if (bitmap==null){
                Toast.makeText(getApplicationContext(), "Please upload your picture", Toast.LENGTH_LONG).show();
                return false;
            }

        } catch (Exception e) {
            Log.d(TAG, "checkData_Exist: " + e.getMessage());
        }
        return true;
    }

    private HashMap<String, String> getHashmap_Values() {
        String strPhone = etPhone.getText().toString().trim();
        strPhone = strPhone.replace("(", "").replace(")", "").replace("-", "").replace(" ", "");

        HashMap<String,String>map=new HashMap<>();
        map.put("name",etUser.getText().toString());
        map.put("phone",strPhone);
        map.put("email",et_Email.getText().toString());
        map.put("password",etPassword.getText().toString());
        map.put("confirmPassword",etConfirmPass.getText().toString());

      //  http://qrcode.iconsulting.com.pk/api/signup?name=Rizwan&phone=+92-333-575-5897&email=rurazza@gmail.com&password=123456&confirmPassword=123456&profilepic=
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
                    if (json.has("Token"))
                    editor.putString(Login_Contstant.AUTH_TOKEN, json.getString("Token"));

                    editor.putString(Login_Contstant.EMAIL, jsonObject.getString(Login_Contstant.EMAIL));
                    editor.putString(Login_Contstant.FIRST_NAME, jsonObject.getString("name"));

                    editor.putString(Login_Contstant.USER_ID, jsonObject.getString("id"));
                    editor.putString(Login_Contstant.USER_KEY, jsonObject.getString("name"));

                    editor.putString(Login_Contstant.PHONE_NO, jsonObject.getString("phone"));

                    editor.putString(Login_Contstant.PASS_KEY, etPassword.getText().toString());
                    editor.putString(Login_Contstant.USER_IMAGE, Network_Stuffs.BASE_URL+jsonObject.getString("image"));

                    editor.commit();

                    Intent intent = new Intent(Registeration_SignUp.this, More_Options.class);
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



    ////Select camera or gallery app for images uploadin
    private void getImageFrom_Gallery() {

        final CharSequence[] charSequences = {"Camera", "Gallery"};

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Registeration_SignUp.this, R.style.AppCompatAlertDialogStyle);
        //alertDialog.setTitle("Take Picture");
        LayoutInflater inflater = getLayoutInflater();
        View titleView = inflater.inflate(R.layout.dialogue_titleview, null);
        alertDialog.setCustomTitle(titleView);
        TextView tv = (TextView) titleView.findViewById(R.id.tvDialogueTitle);
        tv.setText("Take Picture");

        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setItems(charSequences, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                strMedia = charSequences[item].toString();
                if (charSequences[item].equals("Camera")) {

                    if (verifyStoragePermissions(Registeration_SignUp.this)) {
                        return;
                    }
                    if (verifyCameraPermissions(Registeration_SignUp.this)) {
                        return;
                    }

                    takePhoto_Intent();

                } else if (charSequences[item].equals("Gallery")) {
                    if (verifyStoragePermissions(Registeration_SignUp.this)) {
                        return;
                    }
                    take_Image_Gallery();

                }
//                 else if (charSequences[item].equals("Cancel")) {
//
//                    dialogInterface.dismiss();
//                }
            }
        });
        getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
        alertDialog.show();
    }

    private void take_Image_Gallery() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), gallery_Image);
            } catch (ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, gallery_Image);
        }
    }

    private void takePhoto_Intent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        startActivityForResult(intent, CAPTURE_CAMERA);
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(Registeration_SignUp.this,PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

            return true;
        }
        return false;
    }

    /**
     * Checks if the app has permission to write to device Camera
     * If the app does not has permission then the user will be prompted to grant permissions
     */

    private boolean verifyCameraPermissions(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // ActivityCompat.requestPermissions(getApplicationContext(), new String[]{Manifest.permission.CALL_PHONE}, 212);
            ActivityCompat.requestPermissions(Registeration_SignUp.this,new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (strMedia.equalsIgnoreCase("Gallery")) {
                        take_Image_Gallery();
                    } else {
                        verifyCameraPermissions(Registeration_SignUp.this);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied to take photo", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    takePhoto_Intent();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied using camera", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //////////////////////////////////// save picture in sdcard and get path of the pic
    private Uri setImageUri() {
        file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".jpg");
        // Uri imgUri = Uri.fromFile(file);

        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.afroexaentric" + ".provider",
                file);

        imgPath = file.getAbsolutePath();
        return photoURI;
    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }


    @Override
    ///////////////////////// Return result from gallery or camera
    public void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);

        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (requestCode == CAPTURE_CAMERA) {
                try {
                    //  Log.d("ImagePath", "" + imgPath);
                    refreshGallery(file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == gallery_Image) {
                uri = imageData.getData();

                //  performCrop(uri);

                File file = new Compress_Image(Registeration_SignUp.this).saveBitmapToFile(new File(getPath(uri)));
                // listVideos.add(file.getAbsolutePath());
                Log.d(TAG, "onActivityResult: " + file.getAbsolutePath());

                imgPath = file.getAbsolutePath();
                //   imgConvert_ToBase64();
            } 
            
            bitmap = BitmapFactory.decodeFile(imgPath);
            imgUser.setImageBitmap(bitmap);
        } else {
            Log.d("json else onactivity", String.valueOf(resultCode));
            super.onActivityResult(requestCode, resultCode, imageData);
        }

    }
    


    ///////////////////////// Refresh gallery after taking pic from camera
    public void refreshGallery(File mediaFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(mediaFile); //out is your file you saved/deleted/moved/copied
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        } else {
            sendBroadcast(new Intent(
                    android.content.Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }

}
