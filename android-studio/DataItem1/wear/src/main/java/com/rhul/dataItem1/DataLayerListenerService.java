package com.rhul.dataItem1;


import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayerListenerService extends WearableListenerService {

    final static String TAG = "wear-service";
    private static final String DEVICE_KEY = "secret";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents)
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/sync") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String secret = dataMap.getString(DEVICE_KEY);
                    Intent dataIntent = new Intent();
                    dataIntent.setAction(Intent.ACTION_SEND);
                    dataIntent.putExtra("secret",secret);
                    Log.d(TAG,secret);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(dataIntent);

                }
            }
    }

}
