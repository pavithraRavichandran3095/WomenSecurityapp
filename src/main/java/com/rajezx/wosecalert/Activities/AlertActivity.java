package com.rajezx.wosecalert.Activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rajezx.wosecalert.MySingleton;
import com.rajezx.wosecalert.SendSmsActivity;
import com.rajezx.wosecalert.Services.GPSTracker;
import com.rajezx.wosecalert.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AlertActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static final String url = "http://npr.000webhostapp.com/wosecalert/push1.php";
    private String gpsdata;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000;
    private static final long MIN_TIME_BW_UPDATES = 1000;
    private ProgressDialog progressDialog;
    static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/alertwoman.mp3";
    static String pathlatandlong = Environment.getExternalStorageDirectory().getAbsolutePath() + "/latandlong.txt";
    private String message;
    private MediaRecorder mediaRecorder;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Geocoder geocoder;
    private String[] pho;
    String foldername;
    Boolean providerstatus;
    private Button recordbutton, recordstopbutton, sharemylocationviawhatspp;
    List<Address> addressinfo;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_actvitiy);
        progressDialog = new ProgressDialog(this);
        checkdataavailable();
        checkfirebasetoken();
        recordbutton = (Button) findViewById(R.id.recordaudiobutton);

        //sharemylocationviawhatspp.setEnabled(false);

        //  recordstopbutton.setEnabled(false);
        recordbutton.setEnabled(false);
        recordbutton.setText("getting location");
        connectgoogleapi();
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Account[] list = manager.getAccounts();
         foldername = list[0].name.toString();
        foldername = foldername.substring(0, foldername.length() - 10);
       if(!dataconnectionstate()) {
           locationvalues();
       }
       else
       {
           startrecord();
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   stoprecord();
                   recordbutton.setText("Sending Alert");
                   uploadaudiotofirebase(gpsdata);
               }
           }, 5000);

       }
        /* recordbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    startrecord();
                    recordbutton.setText("Recording");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    stoprecord();
                    uploadaudiotofirebase(gpsdata);
                    recordbutton.setText("Record Completed");
                }
                return false;
            }
        });*/
    }
    public void connectgoogleapi() {
        providerstatus = getproviderstatus();
        if (providerstatus) {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API).build();
                Log.i("googleapiclient", "google api client build");
            }
            googleApiClient.connect();
            geocoder = new Geocoder(this, Locale.getDefault());

        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStart() {
        super.onStart();
        connectgoogleapi();
        locationvalues();


    }

    @Override
    public void onPause() {
        super.onPause();
        connectgoogleapi();
    }

    @Override
    public void onResume() {
        super.onResume();
        connectgoogleapi();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        connectgoogleapi();
    }

    private boolean getproviderstatus() {
        LocationManager locationmanager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        return locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onLocationChanged(Location location) {

        JSONObject jsonObject = new JSONObject();

        try {
            List<Address> temp = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            for (Address t : temp) {
                setTitle(t.getLocality() + "," + t.getSubAdminArea() + "," + t.getAdminArea());
                jsonObject.put("locality", t.getLocality());
                jsonObject.put("district", t.getSubAdminArea());
                jsonObject.put("state", t.getAdminArea());
            }
            jsonObject.put("lat", location.getLatitude());
            jsonObject.put("long", location.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        location.getLatitude();
        gpsdata = jsonObject.toString();
        //uploadgpsdatatofirebase(gpsdata);
        recordbutton.setEnabled(true);
        recordbutton.setText("Recording");
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(200000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        /*
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                locationRequest,
                this)*/;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void locationvalues() {

        if (!dataconnectionstate()) {
            GPSTracker gps = new GPSTracker(this);
            gps.getlastknownlocation();
// check if GPS enabled
            if (gps.canGetLocation()) {
                Toast.makeText(this, "getting location", Toast.LENGTH_SHORT).show();
                String googlemapurl = "http://maps.google.com/?q=" + gps.getlastknownlocationlat() + "," + gps.getlastknownlocationlong();
                Log.v("googlemapurlsend", googlemapurl);

                sendsmsintent(googlemapurl);
            } else {
                gps.showSettingsAlert();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkdataavailable() {
        SharedPreferences man = getSharedPreferences("man1", MODE_PRIVATE);
        String[] pho = {
                man.getString("man1number", "").toString(),
                man.getString("man2number", "").toString(),
                man.getString("man3number", "").toString(),
                man.getString("man4number", "").toString(),
                man.getString("man5number", "").toString()
        };
        if (pho[0].length() > 5) {
        } else {
        /*    Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        */}
    }
    public void checkfirebasetoken() {
        SharedPreferences man = getSharedPreferences("firebaseman1", MODE_PRIVATE);
        String[] pho = {
                man.getString("firebaseman1number", "").toString(),
                man.getString("firebaseman2number", "").toString(),
                man.getString("firebaseman3number", "").toString(),
                man.getString("firebaseman4number", "").toString(),
                man.getString("firebaseman5number", "").toString()
        };
        if (pho[0].length() > 5) {
        } else {
            /*Intent intent = new Intent(this,FirebaseDataEntry.class);
            startActivity(intent);
            finish();
            onStop();
            progressDialog.cancel();*/

        }
    }

    public void sendsmsintent(String googlemapsurl) {
        Log.v("sendsmsintent", "1");
        SharedPreferences man = getSharedPreferences("man1", MODE_PRIVATE);
        StringBuilder uri = new StringBuilder("");
        Log.v("sendsmsintent", "1");
        for (int i = 1; i <= 5; i++) {
            String tno = "man" + i + "number";
            String tvalue = man.getString(tno, "").toString();
            uri.append(tvalue);
            uri.append(",");
            Log.v("sendsmsintent", tvalue);
        }
        Log.v("sendsmsintenturi", uri.toString());
       Intent intent = new Intent(this, SendSmsActivity.class);
        intent.putExtra("mapvalue",googlemapsurl);
        intent.putExtra("contactlist",uri.toString());

        startActivity(intent);
        /*Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + uri));
        smsIntent.putExtra("sms_body", "You got a Alert !" +
                "\n And Last Known location is " + googlemapsurl);
        startActivity(smsIntent);*/
        Log.v("smsintent","smsactivit");
    }

    public void startrecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(path);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e + "", Toast.LENGTH_SHORT).show();
            Log.e("exception", e.toString());
        }
    }

    public void stoprecord() {
        recordbutton.setText("Record Audio");
        if (mediaRecorder != null) {
            //mediaRecorder.release();
            mediaRecorder.stop();
            Toast.makeText(this, "stoprecord", Toast.LENGTH_SHORT).show();
            mediaRecorder = null;
        }
    }

    public void uploadaudiotofirebase(final String gpsdata) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Account[] list = manager.getAccounts();
        String foldername = list[0].name.toString();
        foldername = foldername.substring(0, foldername.length() - 10);
        progressDialog.setTitle("Sending Alert ! ");
        progressDialog.show();
        Uri filepath = Uri.fromFile(new File(path));
        storageReference = storageReference.child(foldername).child("alertwoman.mp3");
        Log.i("firebaseaudioupload", filepath.toString());
        storageReference.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.v("downloadpath", downloadUrl.toString());
                        progressDialog.dismiss();
                        recordbutton.setText("Alert send Successfully");
                        uploadgpsdatatofirebase(gpsdata,downloadUrl.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.i("firebaseaudioupload", "failed");
                    }
                });
    }

    public void uploadgpsdatatofirebase(String gpsdata, final String audiodownloadpath) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FileWriter writer = null;
        try {
            writer = new FileWriter(pathlatandlong);

            writer.append(gpsdata);
            writer.flush();

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Account[] list = manager.getAccounts();
        String foldername = list[0].name.toString();
        foldername = foldername.substring(0, foldername.length() - 10);
       /* progressDialog.setTitle("Sending Alerts");
        progressDialog.show();*/
        Uri filepath = Uri.fromFile(new File(pathlatandlong));
        // Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        storageReference = storageReference.child(foldername).child("latandlong.txt");
        Log.i("firebaseaudioupload", filepath.toString());
        storageReference.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        sendpushnotification(downloadUrl.toString(),audiodownloadpath);
//                        Toast.makeText(AlertActivity.this, "onsuccess", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.i("firebaseaudioupload", "failed");
                    }
                });
    }

    private void sendpushnotification(final String downloadurl, final String audiodownloadpath) {
        SharedPreferences man = getSharedPreferences("firebaseman1", MODE_PRIVATE);
        final String firebasetoken1,firebasetoken2,firebasetoken3,firebasetoken4,firebasetoken5;
        firebasetoken1 = man.getString("firebaseman1number", "").toString();
        firebasetoken2 = man.getString("firebaseman2number", "").toString();
        firebasetoken3 = man.getString("firebaseman3number", "").toString();
        firebasetoken4 = man.getString("firebaseman4number", "").toString();
        firebasetoken5 = man.getString("firebaseman5number", "").toString();
        StringRequest pushnotificationrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(AlertActivity.this, response+"push", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() {

                //String tfirebasetoken1 = "" +"d8xBA46TPXA:APA91bEaJw-cIyQqRDvwkJJObv5qe-olSwsAY70yb6gyDLpEQhgerAFazxb9f8n89ZsQW7FmidFp5zCTFRlMlyrBcnx4vd7N4Y69EVf_b9mJFHDDXe6_LUmKGi158HwUwrzCfo0YnmNA";
             //   Log.i("firebasetoken",tfirebasetoken1);
                Map<String,String> params = new HashMap<>();
                params.put("firebasetoken1",firebasetoken1);
                params.put("firebasetoken2",firebasetoken2);
                params.put("firebasetoken3",firebasetoken3);
                params.put("firebasetoken4",firebasetoken4);
                params.put("firebasetoken5",firebasetoken5);
                params.put("firebasepath",downloadurl.toString());
                params.put("firebaseaudiopath",audiodownloadpath);
                Log.v("params",params.toString());

                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(pushnotificationrequest);
    }

    public boolean dataconnectionstate() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void stopalert(View view) {
        onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sendsms(View view) {
        locationvalues();
    }
}
