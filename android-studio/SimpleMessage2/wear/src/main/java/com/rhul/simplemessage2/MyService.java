package com.rhul.simplemessage2;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
            if(messageEvent.getPath().equals("/path")){
                String message = new String(messageEvent.getData());
                Log.i("Leak", message);
            } else if (messageEvent.getPath().equals("/path_empty")){
                String text = new String(messageEvent.getData());
                Log.i("Empty", text);
            }
    }
}
