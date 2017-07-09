package com.maximbravo.chongo2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Maxim Bravo on 7/3/2017.
 */

public class MyService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static GoogleApiClient mGoogleApiClient;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.extractAll();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* MainActivity */,
//                        this /* OnConnectionFailedListener */)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        Intent myIntent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,  0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        long frequency= Utils.frequency * 1000; // in ms
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), frequency, pendingIntent);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            Toast.makeText(this, "The Wearable API is unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "connected!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "connection suspended", Toast.LENGTH_SHORT).show();
    }

}
