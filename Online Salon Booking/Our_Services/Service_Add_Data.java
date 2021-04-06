package com.afroexaentric.Our_Services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afroexaentric.Comman_Stuffs.Check_EditText;
import com.afroexaentric.Comman_Stuffs.Gson_List_Prefs;
import com.afroexaentric.Comman_Stuffs.Keyboard_Close;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Login_SignUp.Login_Contstant;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.Network_Volley.Network_Stuffs;
import com.afroexaentric.R;
import com.afroexaentric.Stripe_Payment.CheckoutActivityJava;
import com.google.android.material.textfield.TextInputLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Service_Add_Data extends AppCompatActivity implements View.OnClickListener, Json_Callback {

    public static final String TIME_SLOT = "time_slot";
    public static final String TIME_ID = "id";

    private JSONObject jsonObject;
    private SharedPreferences sharedPreferences;

    private RecyclerView rv_Categories, rv_Services;
    private Service_Add_Time_Adaptor service_add_time_adaptor;
    private Add_Cart_Adaptor addCartAdaptor;
    private ArrayList<HashMap<String, String>> listTime_Slot;
    private ArrayList<HashMap<String, String>> list;

    private Button btnNext;
    private GeometricProgressView progressView;
    MaterialCalendarView calendarView;

    private EditText et_Address;
    private TextInputLayout ti_Address;

    private double ordertotal;
    private int intQty;
    public  ArrayList<Integer> selectedCount;

    private String strDate, str_Rb="1",strWebData="";
    private AppCompatRadioButton rb_InsideOffice, rb_OutsideOffice;
    private RadioGroup radioGroup;

    Json_Callback json_callback;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_add_data);
        sharedPreferences = getSharedPreferences(Log_Constants.MY_PREFS, MODE_PRIVATE);

         json_callback=this;
        //Toolbar initialization and setup
        toolbar_init();
        ///Intializing widgtes
        initWidgets();
        ///call webservices
        call_Webservices();

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
    public class MySelectorDecorator implements DayViewDecorator, DayFormatter {

        private final Drawable drawable;

        public MySelectorDecorator() {
            drawable = getResources().getDrawable(R.drawable.stroke_rnd_blue);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return true;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
        }

        @NonNull
        @Override
        public String format(@NonNull CalendarDay day) {
            return null;
        }
    }

    public class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
            view.setSelectionDrawable(getResources().getDrawable(R.drawable.rnd_blue_prsd_reg));
        }

        /**
         * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
         */
        public void setDate(LocalDate date) {
            this.date = CalendarDay.from(date);
        }
    }
    ///Intializing widgtes
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initWidgets() {
        progressView = (GeometricProgressView) findViewById(R.id.progressView);

        btnNext = (Button) findViewById(R.id.btnNext);

        rv_Services = (RecyclerView) findViewById(R.id.recyclerview);
        rv_Categories = (RecyclerView) findViewById(R.id.rv_Categories);


        et_Address = (EditText) findViewById(R.id.et);
        ti_Address = (TextInputLayout) findViewById(R.id.ti);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rb_InsideOffice = (AppCompatRadioButton) findViewById(R.id.rb1);
        rb_OutsideOffice = (AppCompatRadioButton) findViewById(R.id.rb2);

        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        calendarView.setDateSelected(CalendarDay.today(), true);
        calendarView.setCurrentDate(CalendarDay.today(), true);
        calendarView.addDecorator(new OneDayDecorator());


        calendarView.setHeaderTextAppearance(R.style.CustomTextAppearance);
        calendarView.setDateTextAppearance(R.style.CustomTextAppearance);
        calendarView.setWeekDayTextAppearance(R.style.CustomTextAppearance);

        // calendarView.setLeftArrow(R.drawable.edit);
        // calendarView.setRightArrow(R.drawable.close);

        calendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .setMinimumDate(CalendarDay.today())
                .commit();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Log.d(TAG, "onDateSelected: " + date.getDay() + "-" + date.getMonth());

                SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date1 = null;
                try {
                    date1 = inFormat.parse("" + date.getDay() + "-" + date.getMonth() + "-" + date.getYear());

                    SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                    String goal = outFormat.format(date1);

                    Log.d(TAG, "onDateSelected: " + goal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                strDate=date.getYear()+"-"+date.getMonth()+"-"+date.getDay();
                widget.invalidateDecorators();
                call_Webservices();
               // calendarView.setDateSelected(CalendarDay.today(), true);
              //  calendarView.setSelectionColor(R.color.red);

            }
        });
        //arraylist
        list = new ArrayList<>();
        selectedCount=new ArrayList<>();
        listTime_Slot = new ArrayList<>();

        CalendarDay date=calendarView.getSelectedDate();
        strDate=date.getYear()+"-"+date.getMonth()+"-"+date.getDay();

        //show fst or last item of the recyclerview partially visible
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rv_Categories);
        //////////// Add data to listview items from selected items (Add to cart))
        add_data_ListView();
        ////////////// Radio Group click
        radio_GroupChecked();

        addCartAdaptor = new Add_Cart_Adaptor(Service_Add_Data.this, list);
        rv_Services.setItemAnimator(new SlideInUpAnimator());
        rv_Services.setAdapter(addCartAdaptor);
        ///click event called
        btnNext.setOnClickListener(this);
    }


    ///click event callback method
    @Override
    public void onClick(View view) {
        ///For hide keyboard
        new Keyboard_Close(Service_Add_Data.this).keyboard_Close_Down();

        if (str_Rb.equalsIgnoreCase("2")) {
            if (!new Check_EditText(Service_Add_Data.this).checkUserame(et_Address, ti_Address, "error")) {
                return;
            }
        }
        if (service_add_time_adaptor.strTimeSlot.equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(), "Select time slot for booking appointment", Toast.LENGTH_SHORT).show();
            return;
        }
       // startActivity(new Intent(Service_Add_Data.this, Book_Appointment.class));
        //https://afroexcentric.com/api/paymentdone
        progressView.setVisibility(View.VISIBLE);
        new Json_Response(Service_Add_Data.this, progressView, getHashmap_Values("add")).call_Webservices(this,"servicebooking");//booking
    }


    ////////////// Radio Group click
    private void radio_GroupChecked() {
        rb_InsideOffice.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb1) {
                    str_Rb = "1";
                    ti_Address.setVisibility(View.GONE);
                } else if (i == R.id.rb2) {
                    str_Rb = "2";
                    ti_Address.setVisibility(View.VISIBLE);
                }
                //log.d("Vehicle type", strVehilceType);
            }
        });
    }

    ///call webservices
    private void call_Webservices() {
        progressView.setVisibility(View.VISIBLE);
        new Json_Response(Service_Add_Data.this, progressView, getHashmap_Values("data")).call_Webservices(this, "timeslot");
    }

    //////////// Add data to listview items from selected items in shopingcard
    private void add_data_ListView() {
        try {
            int b = 0;
            Log.d(TAG, "add_data_ListView: ");
            list = new Gson_List_Prefs(Service_Add_Data.this).getShopinglist();

            //set data to listview ,added to addcart selected item form prevoius screen
            for (int a = 0; a < list.size(); a++) {
                Log.d(TAG, "add_data_ListView: data exist ");
                //is selected boolean array is by deafault false
                //get only true index from array and set data to listview


                //add iselected array index to selectedcount array
                //later when removing item form listview will using selectcount array  in adaptor class
                //to identified how many items remains in listadddcard
                //change isSelected index when removing item,getting element from selectedcount array in adaptor class

                selectedCount.add(a);


              //  int addcard_Value = Integer.parseInt(list.get(a).get(Sub_Service.ADDCART_VALUE));
                int addcard_Value = 3;

                for (int c = 0; c < addcard_Value; c++) {

                    ordertotal += Double.parseDouble(list.get(a).get(Sub_Service.PRICE));

                    b++;//increment b, b var is defining how many items are selected
                    intQty = b;// set tvQty textview, No of total selected products
                }

                //get all selected item listprice and set to footer tv
                // ordertotal += Double.parseDouble(ShopingCard.listShopingCard.get(a).get(ShopingCard.SHOPING_LISTPRICE));

                //  }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error: " + e.getMessage());
        }
    }


    private HashMap<String, String> getHashmap_Values(String data) {
        strWebData=data;
        HashMap<String, String> hashMap = new HashMap<>();
        if (data.equalsIgnoreCase("data")){
            hashMap.put("bookdate", strDate);

        }
        else {

            hashMap.put("requested_date", strDate);
            hashMap.put("requested_time", service_add_time_adaptor.strTimeSlot);
//            for (int i = 0; i < list.size(); i++) {
//                hashMap.put("subservice_id[" + (i) + "]", list.get(i).get(Sub_Service.SERVICE_ID));
//            }
            hashMap.put("subservice_id", list.get(0).get(Sub_Service.SERVICE_ID));
            hashMap.put("location_type", str_Rb);
            if (str_Rb.equalsIgnoreCase("2")){
                hashMap.put("address", strDate);
            }
        }
        hashMap.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, ""));
        hashMap.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));

        Log.d(TAG, "getHashmap_Values: "+hashMap);
      //  user_id,subservice_id[],requested_date,requested_time
        return hashMap;
    }

    @Override
    public void update_Response(JSONObject json) {

        jsonObject = json;
        int success;
        //  scrollView.setVisibility(View.VISIBLE);

        if (jsonObject != null) {
            try {
                success = jsonObject.getInt(Network_Stuffs.SUCCESS);
                if (success == 1) {
                    if (strWebData.equalsIgnoreCase("data")) {
                        categories_Product();
                    }
                    else {
                        //remove addcart array after buying
                        new Gson_List_Prefs(Service_Add_Data.this).removeArray_List();

//                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                        Intent intent=new Intent(Service_Add_Data.this,Booking_Appointment_Data.class);
//                        intent.putExtra("tabmsg",jsonObject.getString("message"));
//                        startActivity(intent);

                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("client_secret",jsonObject.getString("client_secret")).commit();
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Service_Add_Data.this, CheckoutActivityJava.class);
                        startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "update_Response: " + e.getMessage());
            }
        }
    }

    private void categories_Product() {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("timeslots");

            if (jsonArray.length()==0){
                return;
            }
            if (listTime_Slot.size()!=0){
                listTime_Slot.clear();
            }
            for (int a = 0; a < jsonArray.length(); a++) {

                JSONObject jsonItem = jsonArray.getJSONObject(a);

                HashMap<String, String> map = new HashMap<>();

                 map.put(TIME_SLOT, jsonItem.getString(TIME_SLOT));
                 map.put(TIME_ID, jsonItem.getString(TIME_ID));
                listTime_Slot.add(map);

            }

            service_add_time_adaptor = new Service_Add_Time_Adaptor(Service_Add_Data.this, listTime_Slot);
            // rv_Categories.setItemAnimator(new SlideInUpAnimator());
            rv_Categories.setAdapter(service_add_time_adaptor);


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "topSellingProduct: " + e.getMessage());
        }

    }


}