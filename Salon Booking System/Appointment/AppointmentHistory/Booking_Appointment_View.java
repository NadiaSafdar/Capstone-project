package com.afroexaentric.Appointment.AppointmentHistory;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afroexaentric.Activity_Transhion_Enter_Exit.Activity_Transition;
import com.afroexaentric.Appointment.Appointment_Constant;
import com.afroexaentric.LogCalls.LogCalls_Debug;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.afroexaentric.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Booking_Appointment_View extends AppCompatActivity {

    private Booking_Appointment_View_Adaptor booking_appointment_view_adaptor;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String, String>> list;

    private Button btnDownload;
    private TextView tvTotalPrice,tvTotalAmount,tvTax;
    private NestedScrollView rootLayout;

    private String strJson="",strMessage="";
    private JSONObject jsonObject;
    private String strTotalPrice,strTax;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 121;
    private  String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String strFilePath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity enter/exit transition class
        //animating content layout when entering or leaving the sc
        new Activity_Transition(Booking_Appointment_View.this).transition_Acivity();
        setContentView(R.layout.booking_appointment_view);

        strJson = getIntent().getStringExtra(Appointment_Constant.JSON_BUNDLE);
        strMessage=getIntent().getStringExtra(Appointment_Constant.MESSAGE);
        ///Intializing widgtes
        initWidgets();
        //get saved bundle from previous screen on view view button clicked
        get_Bundle_Saved_Data();

    }

    private void initWidgets() {

        rootLayout=(NestedScrollView)findViewById(R.id.layoutRoot);

        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalAmount = (TextView) findViewById(R.id.tvTotal_Amount);
        tvTax=(TextView)findViewById(R.id.tvTax);

        btnDownload=(Button)findViewById(R.id.btnDownload);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        //arraylist
        list = new ArrayList<>();

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                btnDownload.setVisibility(View.GONE);
                new Handler().postAtTime(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        if (!verifyStoragePermissions()) {
                            captureScreen(rootLayout);
                          //  takeScreenshot();
                            btnDownload.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Saved to your memory", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);

            }
        });

    }

    //get saved bundle from previous screen on view view button clicked
    private void get_Bundle_Saved_Data() {
        try {
            jsonObject=new JSONObject(strJson);

            set_Recyler_View();
            setViews_Data();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setViews_Data() {
        tvTotalPrice.setText("$" +strTotalPrice);

        double number = Double.parseDouble(strTax);
        number = Math.round(number * 100);
        number = number/100;

        double totalPrice= number+Double.parseDouble(strTotalPrice);

        tvTax.setText("$ "+number);
        tvTotalAmount.setText("$" + totalPrice);
    }

    //set recyclerview data and call adaptor
    private void set_Recyler_View() {
        try {
            String strRequestedDate = jsonObject.getString("requested_date") + " " + jsonObject.getString("requested_time");
            String strTime = jsonObject.getString("timeago");
             strTotalPrice = jsonObject.getString("total");
             strTax=jsonObject.getString("tax");
            String strLocation = jsonObject.getString("location_type");
            String strStatus = jsonObject.getString("status");
            ///subservice array. Individual service item price,duartion,etc

            JSONArray jsonArray = jsonObject.getJSONArray("subservices");
            for (int b = 0; b < jsonArray.length(); b++) {
                JSONObject jsonItem = jsonArray.getJSONObject(b);

                HashMap<String, String> map = new HashMap<>();

                map.put(Appointment_Constant.SERVICE_NAME, jsonItem.getString("name"));
                map.put(Appointment_Constant.DESCRIPTION, jsonItem.getString("description"));
                map.put(Appointment_Constant.IMAGE_URL, Network_Stuffs.BASE_URL + jsonItem.getString("image"));
                map.put(Appointment_Constant.PRICE, jsonItem.getString("price"));
                map.put(Appointment_Constant.DURATION, jsonItem.getString("duration"));
                map.put(Appointment_Constant.DURATION_TYPE, jsonItem.getString("duration_type"));
                map.put(Appointment_Constant.LOCATION, strLocation);
                map.put(Appointment_Constant.STATUS, strStatus);
                map.put(Appointment_Constant.TOTAL_PRICE, strTotalPrice);
                map.put(Appointment_Constant.TIME_AGO, strTime);
                map.put(Appointment_Constant.DATE, strRequestedDate);
                map.put(Appointment_Constant.PAYMENT_ID, jsonObject.getString(Appointment_Constant.PAYMENT_ID));
                map.put(Appointment_Constant.INVOICE_NO, jsonObject.getString(Appointment_Constant.INVOICE_NO));

                map.put(Appointment_Constant.MESSAGE, strMessage);

                list.add(map);
                // }
            }
            ///call adaptor
            booking_appointment_view_adaptor = new Booking_Appointment_View_Adaptor(Booking_Appointment_View.this, list);
            recyclerView.setAdapter(booking_appointment_view_adaptor);
//            if (list.size() > 0) {
//                schedule_services_adaptor.notifyDataSetChanged();
//                // rvServicePlan.setAdapter(valueMy_trade_adaptor);
//                // tvNo_ServicePlan.setVisibility(View.GONE);
//            } else {
//                //tvNo_ServicePlan.setVisibility(View.VISIBLE);
//
//            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCalls_Debug.d(TAG, "set_Recyler_View: " + e.getMessage());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void printpdfdoc() {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = this.getString(R.string.app_name) + " Document";
        printManager.print(jobName, new adaptor(), null);

        }



    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    //create bitmap from the ScrollView
    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void captureScreen(View view) {

        View v = view.getRootView();
       // View v = getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        // create bitmap screen capture
        ////Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
       // Bitmap bmp=screenShot(v);
       // Bitmap bmp = getScreenshotFromRecyclerView(recyclerView);
        //Bitmap bmp = getBitmapFromView(this.getWindow().findViewById(R.id.layoutRoot)); // here give id of our root layout (here its my FrameLayout's id)
        Bitmap bmp = getBitmapFromView(rootLayout, rootLayout.getChildAt(0).getHeight(), rootLayout.getChildAt(0).getWidth());
        v.setDrawingCacheEnabled(false);
        try {
            createPDf(bmp);
            FileOutputStream fos = new FileOutputStream(new File(Environment
                    .getExternalStorageDirectory().toString(), "SCREEN"
                    + System.currentTimeMillis() + ".png"));
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        printpdfdoc();
//                    }
//                },1000);
//
//            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getOutputMediaFile() {
      /*  File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), et_Regno.getText().toString());*/

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/AfroexCentric/");
        //   mediaStorageDir.mkdir();

        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdir()) {
                Log.d("json", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

        strFilePath= mediaStorageDir.getPath() + File.separator
                + "Pdff" + timeStamp + ".pdf";
       return strFilePath;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPDf(Bitmap bmp) {
        try {

            FileOutputStream fOut=new FileOutputStream(getOutputMediaFile());
//            FileOutputStream fOut = new FileOutputStream(new File(Environment.getExternalStorageDirectory().toString(),
//                    "SCREEN" + System.currentTimeMillis() + ".pdf"));

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(bmp.getWidth(), bmp.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
           bmp.prepareToDraw();
            Canvas c;
            c = page.getCanvas();
            c.drawBitmap(bmp,0,0,null);
            document.finishPage(page);
            document.writeTo(fOut);
            document.close();
            Toast.makeText(getApplicationContext(),"pdf created",Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
//                holder.itemView.setDrawingCacheEnabled(false);
//                holder.itemView.destroyDrawingCache();
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }

        }
        return bigBitmap;
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void takeScreenshot() {
        Date now = new Date();
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public boolean verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(Booking_Appointment_View.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

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
                    Log.d(TAG, "onRequestPermissionsResult: ");
                    takeScreenshot();
                    btnDownload.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Saved to your memory", Toast.LENGTH_SHORT).show();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied to take photo", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private class adaptor extends PrintDocumentAdapter {


            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                InputStream input = null;
                OutputStream output = null;

                try {
                    File file = new File(strFilePath);
                    input = new FileInputStream(file);
                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (FileNotFoundException ee) {
                    //Catch exception
                } catch (Exception e) {
                    //Catch exception
                } finally {
                    try {
                        input.close();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }


                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                callback.onLayoutFinished(pdi, true);
            }
        };
    }



