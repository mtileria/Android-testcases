package com.yduf149.messages;

import android.content.Intent;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.WearableListenerService;

public class MessageService extends WearableListenerService {

    String TAG = "wear-service";
    private static final String DEVICE_KEY = "device";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG,",msg receive on Wear service");

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


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG,"on data change");
        for (DataEvent event : dataEvents)

            if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG,"data changed");
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/sync") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String secret = dataMap.getString(DEVICE_KEY);
                    Log.d(TAG,secret);
                    Intent dataIntent = new Intent();
                    dataIntent.setAction(Intent.ACTION_SEND);
                    dataIntent.putExtra("dataItem",secret);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
    }

}