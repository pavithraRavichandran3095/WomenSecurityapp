package com.rajezx.wosecalert.Services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rajezx.wosecalert.TokenSharedPref;

public class GetFirebaseToken extends FirebaseInstanceIdService {
    public static final String tokenbroadcast ="tokenBroadcast";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FirebaseId", "Refreshed token: " + refreshedToken);
        getApplicationContext().sendBroadcast(new Intent(tokenbroadcast));
        saveToken(refreshedToken);
    }
    public void saveToken(String token)
    {
        TokenSharedPref.getTokenSharedPref(getApplicationContext()).saveToken(token);
    }

}
