package com.maximbravo.chongo2;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.Constants;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Maxim Bravo on 7/3/2017.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        sendCharacter();
    }
    public void sendCharacter() {
        if (MyService.mGoogleApiClient.isConnected()) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.CHARACTER_PATH);

            Word randomWord;
            if (Utils.personal) {
                randomWord = Utils.getRandom(Utils.personalDeck);
            } else {
                randomWord = Utils.getRandom(Utils.hsk1);
            }
            putDataMapRequest.getDataMap().putInt(Constants.LEVEL_KEY, randomWord.getLevel());
            putDataMapRequest.getDataMap().putString(Constants.CHARACTER_KEY, randomWord.getCharacter());
            putDataMapRequest.getDataMap().putString(Constants.PIYIN_KEY, randomWord.getPinyin());
            putDataMapRequest.getDataMap().putString(Constants.DEFINITION_KEY, randomWord.getDefinition());

            PutDataRequest request = putDataMapRequest.asPutDataRequest();

            Wearable.DataApi.putDataItem(MyService.mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            System.out.println("putDataItem status: " + dataItemResult.getStatus().toString());
                        }
                    });
        } else {
            MyService.mGoogleApiClient.connect();
        }
    }
}
