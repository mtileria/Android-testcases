package com.rhul.simplemessage;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        String message = new String(messageEvent.getData());
        Log.i("leak", message);
    }
}