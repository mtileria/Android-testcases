package com.rhul.messages;

import android.content.Intent;
import com.google.android.gms.wearable.MessageEvent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.WearableListenerService;

public class MessageService extends WearableListenerService {

    String TAG = "wear-service";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG,",msg receive on wear service");

        if (messageEvent.getPath().equals("/my_path")) {

            String message = new String(messageEvent.getData());

            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

}