package com.rhul.simpledataitem;

import android.util.Log;


import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/sync") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String text = dataMap.getString("secret");
                    Integer number = dataMap.getInt("number");
                    Log.i("Leak",text);
                    Log.i("Constant", number.toString());

                }
            }
        }
    }
}
