package com.afroexaentric.Profile_User;

import android.Manifest;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afroexaentric.Activity_Transhion_Enter_Exit.Activity_Transition;
import com.afroexaentric.Comman_Stuffs.Check_EditText;
import com.afroexaentric.Comman_Stuffs.Keyboard_Close;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Comman_Stuffs.isNetworkAvailable;
import com.afroexaentric.Login_SignUp.Login_Contstant;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.afroexaentric.R;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class User_Profile extends AppCompatActivity implements View.OnClickListener, Json_Callback {

    private TextView tvterms, tvLogin;
    private ChoosePhoto choosePhoto = null;
    private CircleImageView imgUser;
    private EditText etName, etPhone, etEmail;
    private TextInputLayout tiName,  tiPhone, tiEmail;
    private Button btnUpdate, btnChangePassword;
    private GeometricProgressView progressView;

    private JSONObject jsonObject;

    private EditText[] arrEditextName;
    private TextInputLayout[] arrTExtinpuName;

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

    private SharedPreferences sharedPreferences;

    private boolean isImageUpload = false;
    private Bitmap bitmap=null;
    final int PIC_CROP = 1;
    private String strMedia,strPhone,strUsername,strEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity enter/exit transition class
        //animating content layout when entering or leaving the sc
        new Activity_Transition(User_Profile.this).transition_Acivity();

        setContentView(R.layout.user_profile);

        sharedPreferences=getSharedPreferences(Log_Constants.MY_PREFS,MODE_PRIVATE);

        //Toolbar initialization and setup
        toolbar_init();
        ///Intializing widgtes
        initWidgets();
        /////set data from previous screen,Which is saved in shared preferences
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
        progressView = (GeometricProgressView) findViewById(R.id.progressView);

        imgUser = (CircleImageView) findViewById(R.id.imgUser);

        etName = (EditText) findViewById(R.id.et_Name);
        etPhone = (EditText) findViewById(R.id.et_PhoneNo);
        etEmail = (EditText) findViewById(R.id.et_Email);

        tiName = (TextInputLayout) findViewById(R.id.ti_Name);
        tiPhone = (TextInputLayout) findViewById(R.id.inputLayout_PhoneNo);
        tiEmail = (TextInputLayout) findViewById(R.id.inputLayout_Email);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnChangePassword = (Button) findViewById(R.id.btnChangePass);

        //Array for Editext variable
        arrEditextName = new EditText[]{etName, etPhone, etEmail};
        //Array for TextInputLayout variable
        arrTExtinpuName = new TextInputLayout[]{tiName, tiPhone, tiEmail};

        ////////For phone format
        etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        
        imgUser.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
    }

    /////set data from previous screen,Which is saved in shared preferences
    private void setPrefsData() {
        etName.setText(sharedPreferences.getString(Login_Contstant.FIRST_NAME, ""));
        etPhone.setText(sharedPreferences.getString(Login_Contstant.PHONE_NO,""));
        etEmail.setText(sharedPreferences.getString(Login_Contstant.EMAIL, ""));

        Picasso.with(getApplicationContext()).load(sharedPreferences.getString(Login_Contstant.USER_IMAGE, "http://")).placeholder(R.color.gray).into(imgUser);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUpdate:
                ///For hide keyboard
                new Keyboard_Close(User_Profile.this).keyboard_Close_Down();

                if (checkEditTExtEmpty()) {
                    if (isImageUpload) {
                        //sending data along with image bitmap
                        //calling webservices
                        //getting jsonobject in return via jsoncallback interface
                        progressView.setVisibility(View.VISIBLE);
                        new Json_Response(getApplicationContext(), progressView, getHashmap_Values("data")).multipart_Volley(this, bitmap, "profile");
                    } else {
                        progressView.setVisibility(View.VISIBLE);
                        new Json_Response(getApplicationContext(), progressView, getHashmap_Values("data")).call_Webservices(this, "profile");
                    }
                }
                break;
            case R.id.imgUser:
                ///Select camera or gallery app for images uploadin
                getImageFrom_Gallery();
                break;
            case R.id.btnChangePass:
                new ChangePassword(User_Profile.this).alert_ForgotPass();
                break;

        }
    }

    private boolean checkEditTExtEmpty() {
        if (!new isNetworkAvailable(getApplicationContext()).isConnectivity()) {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            return false;
        }

        for (int a = 0; a < arrEditextName.length; a++) {

            if (!new Check_EditText(User_Profile.this).checkUserame(arrEditextName[a], arrTExtinpuName[a], "message")) {
                return false;
            }
        }
        if (!new Check_EditText(User_Profile.this).isValidEmail(etEmail.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Please type correct email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!new Check_EditText(User_Profile.this).isValidPhoneNumber(etPhone.getText().toString())) {
            Toast.makeText(this, "Please type correct phone number", Toast.LENGTH_LONG).show();
            return false;
        }

//        if (etPassword.getText().length() < 5) {
//            Toast.makeText(getApplicationContext(), "Password must be greater then 6 digits", Toast.LENGTH_LONG).show();
//            return false;
//
//        }
        return true;
    }


    private HashMap<String, String> getHashmap_Values(String data) {
        HashMap<String, String> hashMap = new HashMap<>();

         strUsername = etName.getText().toString();
         strPhone = etPhone.getText().toString();
         strEmail = etEmail.getText().toString();

        hashMap.put("name", strUsername);
        hashMap.put("phone", strPhone);
        hashMap.put("email", strEmail);

        hashMap.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, ""));
        hashMap.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
        Log.d(TAG, "getHashmap_Values: " + hashMap.toString());
        return hashMap;
    }

    @Override
    public void update_Response(final JSONObject json) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {

            if (json != null) {
                jsonObject=json.getJSONObject("user");
                String success = json.getString(Network_Stuffs.SUCCESS);
                if (success.equalsIgnoreCase("1")) {

                    if (isImageUpload) {
                        editor.putString(Login_Contstant.USER_IMAGE, Network_Stuffs.BASE_URL+jsonObject.getString("image"));
                        Log.d(TAG, "update_Response: "+isImageUpload);
                    }
                    isImageUpload = false;


                    editor.putString(Login_Contstant.FIRST_NAME, etName.getText().toString());
                    editor.putString(Login_Contstant.EMAIL,strEmail );
                    editor.putString(Login_Contstant.FIRST_NAME, strUsername);

                    editor.putString(Login_Contstant.USER_KEY, strUsername);
                    editor.putString(Login_Contstant.PHONE_NO, strPhone);

                    editor.putString(Login_Contstant.AUTH_TOKEN, json.getString("Token"));

                    editor.commit();

                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), json.getString("error_message"), Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            editor.commit();
            Log.d("json landing error", e.getMessage());
            e.printStackTrace();
        }
    }

    ////Select camera or gallery app for images uploadin
    private void getImageFrom_Gallery() {

        final CharSequence[] charSequences = {"Camera", "Gallery"};

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(User_Profile.this, R.style.AppCompatAlertDialogStyle);
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

                    if (verifyStoragePermissions(User_Profile.this)) {
                        return;
                    }
                    if (verifyCameraPermissions(User_Profile.this)) {
                        return;
                    }

                    takePhoto_Intent();

                } else if (charSequences[item].equals("Gallery")) {
                    if (verifyStoragePermissions(User_Profile.this)) {
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
                ActivityCompat.requestPermissions(User_Profile.this,PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
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
            ActivityCompat.requestPermissions(User_Profile.this,new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
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
                        verifyCameraPermissions(User_Profile.this);
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


                    HashMap<String, String> map = new HashMap<>();
                   // map.put(DashBoard_Constant.IMAGE, imgPath);
                   // map.put(DashBoard_Constant.ID, "2");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == gallery_Image) {
                uri = imageData.getData();

                //  performCrop(uri);

                File file = new Compress_Image(User_Profile.this).saveBitmapToFile(new File(getPath(uri)));
                // listVideos.add(file.getAbsolutePath());
                Log.d(TAG, "onActivityResult: " + file.getAbsolutePath());

                imgPath = file.getAbsolutePath();
                //   imgConvert_ToBase64();
            } else if (requestCode == PIC_CROP) {
                if (imageData != null) {
                    // get the returned data
                    Bundle extras = imageData.getExtras();
                    // get the cropped bitmap
                    Bitmap selectedBitmap = extras.getParcelable("data");

                    imgUser.setImageBitmap(selectedBitmap);
                }
            }
            isImageUpload = true;
            bitmap = BitmapFactory.decodeFile(imgPath);

            imgUser.setImageBitmap(bitmap);
        } else {
            Log.d("json else onactivity", String.valueOf(resultCode));
            super.onActivityResult(requestCode, resultCode, imageData);
        }

    }


    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
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


//    public String getRealPathFromURI(Uri contentUri) {
//        String[] proj = {MediaStore.Audio.Media.DATA};
//        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
}


