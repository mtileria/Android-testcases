package com.rhul.dataItem1;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;


public class ListenerService extends WearableListenerService {

    final static String TAG = "mobile-service";
    private static final String DEVICE_KEY = "reply";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents)
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/sync_wear") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String secret = dataMap.getString(DEVICE_KEY);
                    Log.d(TAG,"sink: " +  secret); //sink
                }
            }
    }
}
