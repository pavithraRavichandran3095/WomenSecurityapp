package com.rajezx.wosecalert.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rajezx.wosecalert.R;
import com.rajezx.wosecalert.Services.GetFirebaseToken;

public class FirebaseTokenActivity extends AppCompatActivity {


    private static final String shared_pref_name = "FIREBASETOKEN";
    private static final String share_pref_token = "TokenString";
    private TextView firebasetokentextview;
    private BroadcastReceiver tokenbroadcastreceiver;
    private String result = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firebasetokenactivity);

        firebasetokentextview = (TextView) findViewById(R.id.firebasetoken);
        firebasetokentextview.setText("");
        tokenbroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedpref = getSharedPreferences(shared_pref_name, MODE_PRIVATE);
                result = sharedpref.getString(share_pref_token, "");
                Toast.makeText(FirebaseTokenActivity.this, result + "", Toast.LENGTH_SHORT).show();
                firebasetokentextview.setText(result);
            }
        };
        registerReceiver(tokenbroadcastreceiver,new IntentFilter(GetFirebaseToken.tokenbroadcast));



        SharedPreferences sharedpref = getSharedPreferences(shared_pref_name, MODE_PRIVATE);
        result = sharedpref.getString(share_pref_token, "");
        Toast.makeText(FirebaseTokenActivity.this, result + "", Toast.LENGTH_SHORT).show();
        firebasetokentextview.setText(result);

        Log.v("firebasetoken",result);


        /*
        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tokenbroadcastreceiver!=null)
        {
            unregisterReceiver(tokenbroadcastreceiver);
        }
    }


    public void sharefirebasetoken(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "\tFirebase Token\n\t"+result;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Firebase Token");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "FirebaseToken Share"));
    }
}
