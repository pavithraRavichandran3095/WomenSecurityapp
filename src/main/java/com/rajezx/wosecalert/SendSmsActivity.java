package com.rajezx.wosecalert;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SendSmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        String i  = getIntent().getStringExtra("contactlist");
        String v = getIntent().getStringExtra("mapvalue");
        Log.v("contactlist",i);
        Toast.makeText(this, i+"\n\n\n\n"+v, Toast.LENGTH_SHORT).show();
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+i));
        smsIntent.putExtra("sms_body", "You got a Alert !" +
                "\n And Last Known location is " + v);
        startActivity(smsIntent);
    }
}
