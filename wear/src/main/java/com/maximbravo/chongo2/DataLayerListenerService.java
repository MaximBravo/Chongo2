package com.maximbravo.chongo2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

/**
 * Created by Maxim Bravo on 7/3/2017.
 */

public class DataLayerListenerService extends WearableListenerService {
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();

        if (!googleApiClient.isConnected()) {
            ConnectionResult connectionResult = googleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e("onDataChanged", "Service failed to connect to GoogleApiClient.");
                return;
            }
        }

        for (DataEvent event : dataEvents) {
            String path = event.getDataItem().getUri().getPath();
            if (Constants.CHARACTER_PATH.equals(path)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                int level = dataMapItem.getDataMap().getInt(Constants.LEVEL_KEY);
                String character = dataMapItem.getDataMap().getString(Constants.CHARACTER_KEY);
                String pinyin = dataMapItem.getDataMap().getString(Constants.PIYIN_KEY);
                String definition = dataMapItem.getDataMap().getString(Constants.DEFINITION_KEY);
                System.out.println("" + level);
                System.out.println(character);
                System.out.println(pinyin);
                System.out.println(definition);

                Intent displayIntent = new Intent(this, DisplayActivity.class);
                displayIntent.putExtra(Constants.LEVEL_KEY, level);
                displayIntent.putExtra(Constants.CHARACTER_KEY, character);
                displayIntent.putExtra(Constants.PIYIN_KEY, pinyin);
                displayIntent.putExtra(Constants.DEFINITION_KEY, definition);

                Bitmap bitmap = Bitmap.createBitmap(320,320, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(this.getResources().getColor(R.color.colorPrimary));
                NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.color.colorPrimary)
                        .setContentTitle(character);


                notification.extend(new NotificationCompat.WearableExtender()
                        .setBackground(bitmap)
                        .setCustomSizePreset(Notification.WearableExtender.SIZE_FULL_SCREEN)
                        .setDisplayIntent(PendingIntent.getActivity(this, 0, displayIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT)));
                ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).notify(Constants.NOTIFICATION_ID, notification.build());
            }
        }
    }
}
