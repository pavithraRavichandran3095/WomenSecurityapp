package com.rajezx.wosecalert.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rajezx.wosecalert.Activities.FirebaseDataEntry;
import com.rajezx.wosecalert.Activities.FirebaseTokenActivity;
import com.rajezx.wosecalert.Activities.PreferencesActivity;
import com.rajezx.wosecalert.R;

public class PhoneNODataEntry extends AppCompatActivity {

    private EditText man1name, man2name, man3name, man4name, man5name;
    private EditText man1number, man2number, man3number, man4number, man5number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_nodata_entry);
        man1name = (EditText) findViewById(R.id.man1name);
        man2name = (EditText) findViewById(R.id.man2name);
        man3name = (EditText) findViewById(R.id.man3name);
        man4name = (EditText) findViewById(R.id.man4name);
        man5name = (EditText) findViewById(R.id.man5name);
        //sharepreferences

        man1number = (EditText) findViewById(R.id.man1number);
        man2number = (EditText) findViewById(R.id.man2number);
        man3number = (EditText) findViewById(R.id.man3number);
        man4number = (EditText) findViewById(R.id.man4number);
        man5number = (EditText) findViewById(R.id.man5number);
    }
    @Override
    protected void onStart() {
        super.onStart();
        putdataintoeditext();
    }

    public void validdata() {

        if (validname(man1name.getText().toString()) && validname(man2name.getText().toString()) && validname(man3name.getText().toString())
                && validname(man4name.getText().toString()) && validname(man5name.getText().toString()) &&
                validphonenumber(man1number.getText().toString()) && validphonenumber(man2number.getText().toString()) && validphonenumber(man3number.getText().toString())
                && validphonenumber(man4number.getText().toString()) && validphonenumber(man5number.getText().toString())
                )

        {
            SharedPreferences man = getSharedPreferences("man1", MODE_PRIVATE);
            SharedPreferences.Editor manedit = man.edit();
            manedit.putString("man1name", man1name.getText().toString());
            manedit.putString("man1number", man1number.getText().toString());
            manedit.putString("man2name", man2name.getText().toString());
            manedit.putString("man2number", man2number.getText().toString());
            manedit.putString("man3name", man3name.getText().toString());
            manedit.putString("man3number", man3number.getText().toString());
            manedit.putString("man4name", man4name.getText().toString());
            manedit.putString("man4number", man4number.getText().toString());
            manedit.putString("man5name", man5name.getText().toString());
            manedit.putString("man5number", man5number.getText().toString());
            manedit.apply();
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            putdataintoeditext();
        } else {
            Toast.makeText(this, "you must have  5 person contact info 'your data not updated'", Toast.LENGTH_SHORT).show();
        }


    }

    private void putdataintoeditext() {
        SharedPreferences man = getSharedPreferences("man1", MODE_PRIVATE);
        man1name.setText(man.getString("man1name", "").toString());
        man1number.setText(man.getString("man1number", "").toString());
        man2name.setText(man.getString("man2name", "").toString());
        man2number.setText(man.getString("man2number", "").toString());
        man3name.setText(man.getString("man3name", "").toString());
        man3number.setText(man.getString("man3number", "").toString());
        man4name.setText(man.getString("man4name", "").toString());
        man4number.setText(man.getString("man4number", "").toString());
        man5name.setText(man.getString("man5name", "").toString());
        man5number.setText(man.getString("man5number", "").toString());

    }

    public boolean validphonenumber(String pno) {
        if (pno.length() >= 10 && pno.length() < 13) {
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
