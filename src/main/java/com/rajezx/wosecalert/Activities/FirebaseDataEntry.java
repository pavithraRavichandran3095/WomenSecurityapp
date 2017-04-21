package com.rajezx.wosecalert.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rajezx.wosecalert.R;
import com.rajezx.wosecalert.Receiver.AlertReceiver;

public class FirebaseDataEntry extends AppCompatActivity {

    boolean trueandtrue = false;
    boolean trueandfalse = false;
    boolean falseandtrue = false;
    private EditText firebaseman1name, firebaseman2name, firebaseman3name, firebaseman4name, firebaseman5name;
    private EditText firebaseman1number, firebaseman2number, firebaseman3number, firebaseman4number, firebaseman5number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_data_entry);

        firebaseman1name = (EditText) findViewById(R.id.firebaseman1name);
        firebaseman2name = (EditText) findViewById(R.id.firebaseman2name);
        firebaseman3name = (EditText) findViewById(R.id.firebaseman3name);
        firebaseman4name = (EditText) findViewById(R.id.firebaseman4name);
        firebaseman5name = (EditText) findViewById(R.id.firebaseman5name);
        //sharepreferences

        firebaseman1number = (EditText) findViewById(R.id.firebaseman1number);
        firebaseman2number = (EditText) findViewById(R.id.firebaseman2number);
        firebaseman3number = (EditText) findViewById(R.id.firebaseman3number);
        firebaseman4number = (EditText) findViewById(R.id.firebaseman4number);
        firebaseman5number = (EditText) findViewById(R.id.firebaseman5number);

        putdataintoeditext();


    }


    @Override
    protected void onStart() {
        super.onStart();
    }
    public void validdata() {

        if (validname(firebaseman1name.getText().toString()) && validname(firebaseman2name.getText().toString()) && validname(firebaseman3name.getText().toString())
                && validname(firebaseman4name.getText().toString()) && validname(firebaseman5name.getText().toString()) &&
                validphonenumber(firebaseman1number.getText().toString()) && validphonenumber(firebaseman2number.getText().toString()) && validphonenumber(firebaseman3number.getText().toString())
                && validphonenumber(firebaseman4number.getText().toString()) && validphonenumber(firebaseman5number.getText().toString())
                )

        {
            SharedPreferences firebaseman = getSharedPreferences("firebaseman1", MODE_PRIVATE);
            SharedPreferences.Editor firebasemanedit = firebaseman.edit();
            firebasemanedit.putString("firebaseman1name", firebaseman1name.getText().toString());
            firebasemanedit.putString("firebaseman1number", firebaseman1number.getText().toString());
            firebasemanedit.putString("firebaseman2name", firebaseman2name.getText().toString());
            firebasemanedit.putString("firebaseman2number", firebaseman2number.getText().toString());
            firebasemanedit.putString("firebaseman3name", firebaseman3name.getText().toString());
            firebasemanedit.putString("firebaseman3number", firebaseman3number.getText().toString());
            firebasemanedit.putString("firebaseman4name", firebaseman4name.getText().toString());
            firebasemanedit.putString("firebaseman4number", firebaseman4number.getText().toString());
            firebasemanedit.putString("firebaseman5name", firebaseman5name.getText().toString());
            firebasemanedit.putString("firebaseman5number", firebaseman5number.getText().toString());
            firebasemanedit.commit();
            firebasemanedit.apply();
            putdataintoeditext();
        } else {
            Toast.makeText(this, "you must have  5 person contact info 'your data not updated'", Toast.LENGTH_SHORT).show();
        }


    }

    private void putdataintoeditext() {
        SharedPreferences firebaseman = getSharedPreferences("firebaseman1", MODE_PRIVATE);
        firebaseman1name.setText(firebaseman.getString("firebaseman1name", "").toString());
        firebaseman1number.setText(firebaseman.getString("firebaseman1number", "").toString());
        firebaseman2name.setText(firebaseman.getString("firebaseman2name", "").toString());
        firebaseman2number.setText(firebaseman.getString("firebaseman2number", "").toString());
        firebaseman3name.setText(firebaseman.getString("firebaseman3name", "").toString());
        firebaseman3number.setText(firebaseman.getString("firebaseman3number", "").toString());
        firebaseman4name.setText(firebaseman.getString("firebaseman4name", "").toString());
        firebaseman4number.setText(firebaseman.getString("firebaseman4number", "").toString());
        firebaseman5name.setText(firebaseman.getString("firebaseman5name", "").toString());
        firebaseman5number.setText(firebaseman.getString("firebaseman5number", "").toString());

    }

    public boolean validphonenumber(String pno) {
        if (pno.length() >= 80 && pno.length() < 200) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validname(String name) {
        if (name.length() > 3) {
            return true;
        } else {
            return false;
        }
    }

    public void save(View view) {
        validdata();

    }
    
}
