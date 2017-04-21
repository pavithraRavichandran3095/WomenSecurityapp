package com.rajezx.wosecalert;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by py on 4/5/2017.
 */

public class TokenSharedPref {
    private static final String shared_pref_name = "FIREBASETOKEN";
    private static final String share_pref_token = "TokenString";

    private static TokenSharedPref tokenInstance;
    private Context context;

    public TokenSharedPref(Context ctx) {
        context = ctx;
    }

    public static synchronized TokenSharedPref getTokenSharedPref(Context context) {
        if (tokenInstance == null)
        {
        tokenInstance = new TokenSharedPref(context);
        }
        return tokenInstance;
    }

    public boolean saveToken(String tokenValue)
    {
        Log.v("firebaseid",tokenValue);
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_pref_name,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(share_pref_token,tokenValue);
        edit.apply();
        return true;
    }
    public String gettoken()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_pref_name,Context.MODE_PRIVATE);
        String tokenvalue = sharedPreferences.getString(share_pref_token,"");
        Log.v("gettokenfirebaseid",gettoken());
        return tokenvalue;
    }
}
