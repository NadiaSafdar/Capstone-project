package com.afroexaentric.Appointment.AppointmentHistory;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.afroexaentric.Activity_Transhion_Enter_Exit.Activity_Transition;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.More_Options;
import com.afroexaentric.R;
import com.google.android.material.tabs.TabLayout;

import net.bohush.geometricprogressview.GeometricProgressView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Booking_Appointment_Data extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private GeometricProgressView progressView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int numOfActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity enter/exit transition class
        //animating content layout when entering or leaving the sc
        new Activity_Transition(Booking_Appointment_Data.this).transition_Acivity();
        setContentView(R.layout.booking_appointment_data);

        sharedPreferences = getSharedPreferences(Log_Constants.MY_PREFS, MODE_PRIVATE);

        //Toolbar initialization and setup
        toolbar_init();
        ///Intializing widgtes
        initWidgets();

////////////////////////////////////
        ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );

        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        Log.d(TAG, "taskList: "+taskList.get(0).topActivity.getShortClassName());
        numOfActivities=taskList.get(0).numActivities;
        Log.d(TAG, "onCreate: Booking_Appointment_Data");
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
                if (numOfActivities>3){
                    Intent intent = new Intent(Booking_Appointment_Data.this, More_Options.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    finish();
                }
            }
        });

    }

    /// Initilzation widgtes
    private void initWidgets() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        if (intent!=null){
            String strBundle=intent.getStringExtra("tabmsg");
            if (strBundle != null && strBundle.equalsIgnoreCase("Thank you for booking!")) {
                tabLayout.getTabAt(1).select();

            }
        }

       // viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
       // tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdaptor adapter = new PagerAdaptor(getSupportFragmentManager());
        adapter.addFragment(new Pending_Appointment(), "Pending");
        adapter.addFragment(new Accepted_Appointment(), "Accepted");
        adapter.addFragment(new Rejected_Appointment(), "Rejected");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: "+numOfActivities);
        if (numOfActivities>3){
            Intent intent = new Intent(Booking_Appointment_Data.this, More_Options.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}






//    ////////////// Tabs with view Pager,swipe view
//    private void tabsViewPager() {
//        tabLayout.addTab(tabLayout.newTab().setText("Services Remaining"));
//        tabLayout.addTab(tabLayout.newTab().setText("Service Used"));
//
//        ///when  pressed back button ,screen will be not blank using getChildFragmentManager()
//        PagerAdaptor adapter = new PagerAdaptor(getChildFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(adapter);
//
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//                Log.d("json TAb Position", "" + tab.getPosition());
//                if (tab.getPosition() == 0) {
//                    viewPager.getLayoutParams().height = getTotalHeightofListView()+120;
//                    layoutSer_Remaining.setVisibility(View.VISIBLE);
//                    layoutSer_history.setVisibility(View.GONE);
//                } else {
//                    viewPager.getLayoutParams().height = AdminServiceHistory.serviceHist_LV_Height+120;
//                    layoutSer_Remaining.setVisibility(View.GONE);
//                    layoutSer_history.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });
//
////        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.tab_text_with_icon, null);
////        tabOne.setText("Service Remaining");
////        tabOne.setTextColor(ContextCompat.getColor(getActivity(), R.color.lightblue));
////        tabOne.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circle_yellow, 0, 0, 0);
////        tabOne.setSelected(true);  //This will make your tab by default selected
////        tabLayout.getTabAt(0).setCustomView(tabOne);
//
//    }
