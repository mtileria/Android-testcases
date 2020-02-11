package com.rhul.simplemessage3;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/unreachable")){
            String message = new String(messageEvent.getData());
            Log.i("Leak", message);
        }
}}
