package com.afroexaentric.Stripe_Payment;

import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

public class EphemeralKey_Get implements EphemeralKeyProvider {

    @Override
    public void createEphemeralKey(@NonNull @Size(min = 4) String apiVersion, @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
        final Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("api_version", apiVersion);

    }
}
