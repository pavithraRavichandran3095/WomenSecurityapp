

package com.rajezx.wosecalert.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.EntityIterator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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
import com.rajezx.wosecalert.Receiver.AlertReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    boolean trueandtrue = false;
    private ProgressDialog progressDialog;
    boolean trueandfalse = false;
    boolean falseandtrue = false;
    private  DownloadManager downloadmanager;
    private MediaRecorder mediaRecorder;
    private TextView firebasetokentextview;
    private String audiodownloadurl = "";
    private String txtdownloadurl = "";
    private MediaPlayer mPlayer;
    private TextView localitytv;
    private TextView subadminareatv;
    private TextView adminareatv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*localitytv = (TextView) findViewById(R.id.locality);
        subadminareatv = (TextView) findViewById(R.id.subadmin);
        adminareatv = (TextView) findViewById(R.id.admin);*/
        progressDialog = new ProgressDialog(this);
        SharedPreferences checksharepref = getSharedPreferences("pref", MODE_PRIVATE);
        String tvalue = checksharepref.getString("sendalert", "");
        Log.d("tvalue", tvalue);

        if (tvalue.isEmpty()) {
            Log.d("tvalue", "empty");
            SharedPreferences.Editor edit = checksharepref.edit();
            edit.putString("sendalert", "false");
            edit.putString("receivealert", "false");
            edit.apply();
        } else {
            String treceive = checksharepref.getString("sendalert", "");
            String tsend = checksharepref.getString("receivealert", "");
            Log.d("tvalue", treceive);
            Log.d("tvalue", tsend);
            if (treceive.equals("true") && tsend.equals("true")) {
                trueandtrue = true;
                Log.d("trueandtrue", trueandtrue + "");
            } else if (treceive.equals("true") && tsend.equals("false")) {
                trueandfalse = true;
                ComponentName component = new ComponentName(getApplicationContext(), AlertReceiver.class);
                getApplicationContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                Log.d("trueandfalse", trueandfalse + "");
            } else if (treceive.equals("false") && tsend.equals("true")) {
                falseandtrue = true;

                Log.d("falseandtrue", falseandtrue + "");
            }
        }
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object latandlongpath = getIntent().getExtras().get("msg");
                Object audiopath = getIntent().getExtras().get("audio");
                if (audiopath != null) {
                    Log.v("downloadpath", audiopath.toString());
                    Log.v("downloadpath", latandlongpath.toString());
                    DownloadData(audiopath.toString(),"womanalert.mp3");
                    DownloadData(latandlongpath.toString(),"latandlong.txt");
                }
            }
            }
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
            };

            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        firebasetokentextview = (TextView) findViewById(R.id.header);
    }

    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (trueandtrue) {
        } else if (trueandfalse) {
            menu.findItem(R.id.offline).setVisible(false);
            menu.findItem(R.id.Online).setVisible(false);
        } else if (falseandtrue) {
            menu.findItem(R.id.offline).setVisible(false);
            menu.findItem(R.id.Online).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sendandreceive, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.preferences:
                onPref();
                return true;
            case R.id.firebasetoken:
                onFirebaseToken();
                return true;
            case R.id.Online:
                onlineemergency();
                return true;
            case R.id.offline:
                offlinemergency();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void offlinemergency() {
        Intent intent = new Intent(this,PhoneNODataEntry.class);
        startActivity(intent);
    }

    private void onlineemergency() {
        Intent intent = new Intent(this, FirebaseDataEntry.class);
        startActivity(intent);
    }

    private void onPref() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    private void onFirebaseToken() {
        Intent intent = new Intent(this, FirebaseTokenActivity.class);
        startActivity(intent);
    }

    private long DownloadData(String url,String filename) {
        File file = new File("/sdcard/Android/data/com.rajezx.wosecalert/files/storage/emulated/0/womanalert.mp3");
        File file1 = new File("/sdcard/Android/data/com.rajezx.wosecalert/files/storage/emulated/0/latandlong.txt");

        if(file.exists())
        {
            file.delete();
        }
        if(file1.exists())
        {
            file1.delete();
        }

        Uri uri = Uri.parse(url);
        long downloadReference;
        // Create request for android download manager
        downloadmanager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Data Download");

        //Setting description of request
        request.setDescription("Android Data download using DownloadManager.");

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory


        request.setDestinationInExternalFilesDir(MainActivity.this,
                Environment.getExternalStorageDirectory().toString(),filename);

        //Enqueue download and save into referenceId
        downloadReference = downloadmanager.enqueue(request);
        return downloadReference;
    }

    /*public void playaudio(View view) {
        Uri myUri1 = Uri.parse(audiopath);
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(getApplicationContext(), myUri1);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        }
        mPlayer.start();
    }
    */


    public  void playaudio(View view)
    {
        String fileName = "womanalert.mp3";
        File s1dCardDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String sdCardDir  = "/sdcard/Android/data/com.rajezx.wosecalert/files/storage/emulated/0/";
        //String sdCardDir = getApplicationContext().getFilesDir()+"/storage/emulated/0/";
        //File sdCardDir = Environment.getExternalStorageDirectory();
        //File sdCardDir = getApplicationContext().getFilesDir();
        // Get The Text file
        File audiopath= new File(sdCardDir, fileName);
        Log.v("audiopath",audiopath.toString());
        Toast.makeText(this, audiopath+"audio playing", Toast.LENGTH_SHORT).show();
        //String audiopath = Environment.getExternalStorageDirectory()+"/womanalert.mp3";
        Toast.makeText(this, audiopath+"", Toast.LENGTH_SHORT).show();
        Uri myUri1 = Uri.parse(audiopath.toString());

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(getApplicationContext(), myUri1);
        } catch (IllegalArgumentException e) {
            Log.e("uriincorrect",e.toString());
            Toast.makeText(getApplicationContext(), e+"You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {

            Log.e("uriincorrect",e.toString());
            Toast.makeText(getApplicationContext(), e+"You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {

            Log.e("uriincorrect",e.toString());
            Toast.makeText(getApplicationContext(), e+"You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {

            Log.e("uriincorrect",e.toString());
            e.printStackTrace();
        }
        try {
            mPlayer.prepare();
        } catch (IllegalStateException e) {

            Log.e("uriincorrect",e.toString());
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {

            Log.e("uriincorrect",e.toString());
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        }
        mPlayer.start();
    }
    private void stopplay()
    {
        if(mPlayer!=null && mPlayer.isPlaying()){
            mPlayer.stop();
        }
    }
    public void viewinmap(View view) {
        Toast.makeText(this, "map", Toast.LENGTH_SHORT).show();
       String jsonvalue =  getfilecontents();
        Log.v("jsonvalue",jsonvalue);
      //  Toast.makeText(this, jsonvalue+"", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonObject = new JSONObject(jsonvalue);
            String lat = jsonObject.getString("lat");
            String log = jsonObject.getString("long");
            Toast.makeText(this, lat+"long->"+log, Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getSharedPreferences("latandlongvalue",MODE_PRIVATE);
            SharedPreferences.Editor latandlongedit = sharedPreferences.edit();
            latandlongedit.putString("latvalue",lat);
            latandlongedit.putString("logvalue",log);
            latandlongedit.apply();
            Log.v("latvalue",lat);
            Log.v("longvalue",log);
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

  /*  public String getfilecontents(String lat) {
//Read text from file
        String tvalue = "";
        File dir = Environment.getExternalStorageDirectory();
        //File yourFile = new File(dir, "path/to/the/file/inside/the/sdcard.ext");

        //Get the text file
        File file = new File(dir, "latandlong-1.txt");
        // i have kept text.txt in the sd-card

        if (file.exists())   // check if file exist
        {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('n');
                }
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }
            tvalue = text.toString();
        }
        return tvalue.toString();
    }*/
    public String getfilecontents() {
        String fileName = "latandlong.txt";
        // Get the dir of SD Card
        //File sdCardDir = Environment.getExternalStorageDirectory();
        //File file = new File("/sdcard/Android/data/com.rajezx.wosecalert/files/storage/emulated/0/womanalert.mp3");

        // Get The Text file
        File latandlongpath = new File("/sdcard/Android/data/com.rajezx.wosecalert/files/storage/emulated/0/", fileName);
        //String latandlongpath = Environment.DIRECTORY_NOTIFICATIONS+"/latandlong.txt";
        //Log.v("getcontentpath",latandlongpath);
        Toast.makeText(this, latandlongpath+"", Toast.LENGTH_SHORT).show();
        // Read the file Contents in a StringBuilder Object
        StringBuilder text = new StringBuilder();
        try {

            BufferedReader reader = new BufferedReader(new FileReader(latandlongpath));

            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line + '\n');
            }
            reader.close();
        } catch (IOException e) {
            Log.v("fileerror",e.toString());
            Toast.makeText(this, e.toString()+"", Toast.LENGTH_SHORT).show();
            /*
            Log.e("C2c", "Error occured while reading text file!!");*/

        }
        return text.toString();
    }
}
