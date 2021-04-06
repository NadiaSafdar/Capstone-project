package com.afroexaentric.Stripe_Payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afroexaentric.Alert_Dialogue.AlertDialogue;
import com.afroexaentric.Appointment.AppointmentHistory.Booking_Appointment_Data;
import com.afroexaentric.Comman_Stuffs.Log_Constants;
import com.afroexaentric.Login_SignUp.Login_Contstant;
import com.afroexaentric.Network_Volley.Json_Callback;
import com.afroexaentric.Network_Volley.Json_Response;
import com.afroexaentric.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.view.CardMultilineWidget;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;
public class CheckoutActivityJava extends AppCompatActivity implements Json_Callback {

    /**
     * This example collects card payments, implementing the guide here: https://stripe.com/docs/payments/accept-a-payment#android
     * <p>
     * To run this app, follow the steps here: https://github.com/stripe-samples/accept-a-card-payment#how-to-run-locally
     */
    // 10.0.2.2 is the Android emulator's alias to localhost
    private static final String BACKEND_URL = "http://qvaganza.com/api/get_ephemeral_key";

    private String paymentIntentClientSecret;
    private Stripe stripe;

   private Button payButton;
    Json_Callback json_callback;
    private CardMultilineWidget cardInputWidget;
    private String strService_Data;
    private String strToken;

    private GeometricProgressView progressView;
    SharedPreferences sharedPreferences;
    private String strID;
    private Toolbar toolbar;

    private boolean isSuccess=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_java);

        //Toolbar initialization and setup
        toolbar_init();

        progressView=(GeometricProgressView)findViewById(R.id.progressView);

        sharedPreferences=getSharedPreferences(Log_Constants.MY_PREFS,MODE_PRIVATE);
        cardInputWidget = findViewById(R.id.cardInputWidget);

        startCheckout();
//        new Json_Response(CheckoutActivityJava.this, payButton, getHashmap_Values("data")).call_Webservices(this,
//                "get_ephemeral_key");

        json_callback = this;
//        new Json_Response(CheckoutActivityJava.this, payButton, getHashmap_Values("charge")).call_Webservices(json_callback,
//                "charge");


    }
    //Toolbar initialization and setup
    private void toolbar_init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

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
                if (isSuccess) {
                    Intent intent = new Intent(CheckoutActivityJava.this, Booking_Appointment_Data.class);
                    startActivity(intent);
                }
                else {
                    finish();
                }
            }
        });

    }
    private HashMap<String, String> getHashmap_Values(String data) {
        progressView.setVisibility(View.VISIBLE);
        strService_Data = data;
        HashMap<String, String> map = new HashMap<>();

        if (data.equalsIgnoreCase("charge")) {
            map.put("amount", "2");
            map.put("currency", "usd");

        }
        map.put("clientSecret", sharedPreferences.getString("client_secret",""));
        map.put("id", strID);
        map.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, ""));
        map.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, ""));
        return map;
    }

    private void startCheckout() {
        // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.

        // Hook up the pay button to the card widget and stripe instance
        payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: " + cardInputWidget.getCard().getNumber());
//                new Json_Response(CheckoutActivityJava.this, payButton, getHashmap_Values("charge")).call_Webservices(json_callback,
//                        "charge");
//                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
//                if (params != null) {
//                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
//                    stripe.confirmPayment(CheckoutActivityJava.this, confirmParams);
//                }
                paymentIntentClientSecret = sharedPreferences.getString("client_secret","");

                // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
                /// PaymentConfiguration.getInstance(context).getPublishableKey()
                stripe = new Stripe(getApplicationContext(), "pk_live_51Hp5HDGw0dLozXrH9eu745U8VjtmEbQJ4Vh80XFQCmbvdFD9ein4druTjGq3EtN3IEZt9T4Cekn9T0tIc1Cqspus00LwYup7ZG");

                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                Log.d(TAG, "onClick: "+cardInputWidget.getPaymentMethodCreateParams()+" params\n"+params);
                if (params != null) {
                    progressView.setVisibility(View.VISIBLE);
                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                    stripe.confirmPayment(CheckoutActivityJava.this, confirmParams);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(CheckoutActivityJava.this));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void update_Response(JSONObject jsonObject) {
        payButton.setVisibility(View.VISIBLE);
        try {
            Log.d("json", "update_Response: " + jsonObject.toString());

            // For added security, our sample app gets the publishable key from the server
            // String stripePublishableKey = jsonObject.getString("id");
            paymentIntentClientSecret = jsonObject.getString("client_secret");

            // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
            /// PaymentConfiguration.getInstance(context).getPublishableKey()
            stripe = new Stripe(getApplicationContext(), Objects.requireNonNull("pk_test_lmE9dZLMZwUIP0zjiZW64tjn00BaFeEuKp"));
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(CheckoutActivityJava.this, confirmParams);
            }

        } catch (Exception e) {
            Log.d("json", "update_Response: " + e.getMessage());
        }
    }


    private static final class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<CheckoutActivityJava> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivityJava activity) {
            activityRef = new WeakReference<>(activity);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.progressView.setVisibility(View.GONE);
            Log.d(TAG, "onSuccess: " + result.getIntent().toString());
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Log.d(TAG, "onSuccess: gson " + gson.toJson(paymentIntent));
            try {
                JSONObject jsonObject=new JSONObject(gson.toJson(paymentIntent));
                activity.strID=jsonObject.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status == PaymentIntent.Status.Succeeded) {
                activity.isSuccess=true;
                new Json_Response(activity, activity.progressView, activity.getHashmap_Values("payment")).call_Webservices(activity.json_callback,
                        "paymentdone");
                // Payment completed successfully
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//                Log.d(TAG, "onSuccess: gson "+gson.toJson(paymentIntent));
                new AlertDialogue(activity).dialogueAbout(activity,"Your payment is complete","complete");
//                activity.displayAlert(
//                        "Payment completed",
//                        gson.toJson(paymentIntent),
//                        true
//                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                new AlertDialogue(activity).dialogueAbout(activity,paymentIntent.getLastPaymentError().getMessage(), "error");
//                activity.displayAlert(
//                        "Payment failed",
//                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
//                        false
                //);
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckoutActivityJava activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.progressView.setVisibility(View.GONE);
            Log.d(TAG, "onSuccess: gson error " + e.toString());
            // Payment request failed – allow retrying using the same payment method
            new AlertDialogue(activity).dialogueAbout(activity,"Error\n"+e.toString(), "error");
            //activity.displayAlert("Error", e.toString(), false);
        }
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message,
                              boolean restartDemo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        if (restartDemo) {
            builder.setPositiveButton("Restart demo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                    cardInputWidget.clear();
                    startCheckout();
                }
            });

        } else {
            builder.setPositiveButton("Ok", null);
        }
        builder.create().show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isSuccess) {
            Intent intent = new Intent(CheckoutActivityJava.this, Booking_Appointment_Data.class);
            startActivity(intent);
        }
        else {
            finish();
        }

    }
}