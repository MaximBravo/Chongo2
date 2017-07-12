package com.maximbravo.chongo2;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.events.ChangeListener;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import com.google.android.gms.drive.Drive;
import static android.app.ActivityManager.*;

public class MainActivity extends AppCompatActivity
//        implements
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener
{

    private static final String NUMBER_KEY = "number";
    private static final int RESOLUTION_CODE = 2345;
    private TextView textView;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.context = this;
        textView = (TextView) findViewById(R.id.name);
        Intent i = getIntent();
        if(i != null) {
            if(i.hasExtra("name")) {
                textView.setText(i.getStringExtra("name"));
            }
        }
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//        mGoogleApiClient.connect();
        Button openFile = (Button) findViewById(R.id.open_file);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FileExtractor.class);
                startActivity(intent);
            }
        });
        serviceStuff();
        //this.stopService(service);
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }
    private void serviceStuff() {

        final Intent service = new Intent(this, MyService.class);
        final Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceRunning()) {
                    MainActivity.this.stopService(service);
                    Intent myIntent = new Intent(MainActivity.this, MyBroadcastReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,  0, myIntent, 0);

                    AlarmManager alarmManager = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    start.setText("Start");
                } else {
                    MainActivity.this.startService(service);
                    start.setText("Stop");
                }

            }
        });
    }



    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.maximbravo.chongo2.MyService".equals(service.service.getClassName())) {
                Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        Toast.makeText(this, "service is running", Toast.LENGTH_SHORT).show();
        return false;
    }

//    private void printFileSystemStuff() throws IOException {
////        String path = Environment.().getPath();
////        System.out.println(path);
////        File directory = new File(path);
////        File[] files = directory.listFiles();
////        //Log.i("Files", "Size: "+ files.length);
////        if(files != null) {
////            for (File f : files) {
////                Log.i("Files", "FileName:" + f.getName());
////            }
////        }
//        FileInputStream in = openFileInput(new File("/sdcard/Download/DuChinese_2017-07-03_1499123459.csv").getAbsolutePath());
//        InputStreamReader inputStreamReader = new InputStreamReader(in);
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            sb.append(line);
//        }
//        System.out.println(sb.toString());
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        System.out.println("connected!!!");
//        openFile();
//    }
//
//    private void openFile() {
//        System.out.println("mGoogleApiClient connected: " + mGoogleApiClient.isConnected());
//        Query query = new Query.Builder()
//                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
//                .build();
//        Drive.DriveApi.query(mGoogleApiClient, query)
//                .setResultCallback(metadataCallback);
//    }
//
//    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new
//            ResultCallback<DriveApi.MetadataBufferResult>() {
//                @Override
//                public void onResult(DriveApi.MetadataBufferResult result) {
//                    if (!result.getStatus().isSuccess()) {
//                        Log.e("Mainacivity","Problem while retrieving results");
//                        return;
//                    }
//
//                }
//            };
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//    private int REQ_OPEN = 999;
//    private void filePicker() throws IntentSender.SendIntentException {
//        if (mGoogleApiClient.isConnected()) {
//            IntentSender i = Drive.DriveApi
//                    .newOpenFileActivityBuilder()
//                    .build(mGoogleApiClient);
//
//            startIntentSenderForResult(i, REQ_OPEN, null, 0, 0, 0);
//
//        }
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == REQ_OPEN) {
//            if(resultCode == RESULT_OK) {
//                System.out.println("FilePicker ok");
//                DriveId driveId = (DriveId) data.getParcelableExtra(
//                        OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
//                System.out.println(driveId.encodeToString());
//            }
//        }
//
//        if(requestCode == RESOLUTION_CODE) {
//            System.out.println("in activityResult");
//            if(resultCode == RESULT_OK) {
//                System.out.println("Result ok");
//                mGoogleApiClient.connect();
//            } else if (resultCode == RESULT_FIRST_USER) {
//                System.out.println("First User");
//            } else if (resultCode == RESULT_CANCELED) {
//                System.out.println("Result Cancelled");
//                data.toString();
//            }
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        System.out.println("Connection failed");
//        if(connectionResult.hasResolution()) {
//            try {
//                connectionResult.startResolutionForResult(this, RESOLUTION_CODE);
//                System.out.println("Started resulutionfor result");
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
