package com.rajezx.wosecalert.Activities;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rajezx.wosecalert.R;

public class ViewAlert extends AppCompatActivity {

    private static final int TIMEOUT_CONNECTION = 10000;
    private static final int TIMEOUT_SOCKET = 10000;
    private DownloadManager downloadManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alert);
/*

        String audiopath = getIntent().getExtras().getString("audiopath");
        String txtpath = getIntent().getExtras().getString("txtpath");
*/

        DownloadData("https://firebasestorage.googleapis.com/v0/b/wosecalert2.appspot.com/o/rajeszxweb%2Falertwoman.mp3?alt=media&token=9d477f0a-cc67-4be2-ac1b-4824cb1c2c15","alertwoman.mp3");
        DownloadData("https://firebasestorage.googleapis.com/v0/b/wosecalert2.appspot.com/o/rajeszxweb%2Flatandlong.txt?alt=media&token=85a6fa78-2253-4891-9b4b-11d1b3d93c70","latandlong.txt");
    }

    private long DownloadData(String url,String filename) {
        Uri uri = Uri.parse(url);
        long downloadReference;
        // Create request for android download manager
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Data Download");

        //Setting description of request
        request.setDescription("Android Data download using DownloadManager.");

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory
            request.setDestinationInExternalFilesDir(ViewAlert.this,
                    Environment.DIRECTORY_DOWNLOADS, filename);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);
        return downloadReference;
    }
}