package com.rajezx.wosecalert.Activities;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.rajezx.wosecalert.R;
import com.rajezx.wosecalert.Receiver.AlertReceiver;

public class PreferencesActivity extends AppCompatActivity {

    private CheckBox sendalert;
    private CheckBox receivealert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        sendalert = (CheckBox) findViewById(R.id.SendAlert);
        receivealert = (CheckBox) findViewById(R.id.receiveonlyalert);

        SharedPreferences checksharepref = getSharedPreferences("pref", MODE_PRIVATE);
        String result = checksharepref.getString("sendalert", "");
        if (result.equals("true")) {
            sendalert.setChecked(true);
        } else {
            sendalert.setChecked(false);
            ComponentName component = new ComponentName(getApplicationContext(), AlertReceiver.class);
            getApplicationContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }

        String resultreceive = checksharepref.getString("receivealert", "");
        if (resultreceive.equals("true")) {
            receivealert.setChecked(true);
        } else {
            receivealert.setChecked(false);
        }
        sendalert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (compoundButton.isChecked()) {

                    SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("sendalert", "true");
                    editor.apply();
                    ComponentName component = new ComponentName(getApplicationContext(), AlertReceiver.class);
                    getApplicationContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);



                    //              Toast.makeText(PreferencesActivity.this, "sendalert true", Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("sendalert", "false");
                    editor.apply();
                    String ns = Context.NOTIFICATION_SERVICE;
                    NotificationManager nMgr = (NotificationManager) getSystemService(ns);
                    nMgr.cancel(0);

                    ComponentName component = new ComponentName(getApplicationContext(), AlertReceiver.class);
                    getApplicationContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                    //                Toast.makeText(PreferencesActivity.this, "sendalert false", Toast.LENGTH_SHORT).show();
                }
            }
        });
        receivealert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (compoundButton.isChecked()) {

                    SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("receivealert", "true");
                    editor.apply();
                    //                  Toast.makeText(PreferencesActivity.this, "receive true", Toast.LENGTH_SHORT).show();

                } else {

                    SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("receivealert", "false");
                    editor.apply();
//                    Toast.makeText(PreferencesActivity.this, "receive false", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
