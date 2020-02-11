package com.rhul.assets;

import android.util.Log;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {

    private static final String DEVICE_KEY = "secret_1";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents){
            if (event.getType() == DataEvent.TYPE_CHANGED) {
              DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals("/path_1")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Asset asset = dataMap.getAsset(DEVICE_KEY);
                    Log.i("Leak-1", asset.getData().toString());
                } else if ("/path_2".equals(item.getUri().getPath())){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Asset asset2 = dataMap.getAsset("secret_2");
                    Log.i("Leak-2",asset2.getData().toString());
                }
            }
        }
    }
}
