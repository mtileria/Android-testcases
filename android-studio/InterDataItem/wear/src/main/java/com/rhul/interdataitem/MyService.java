package com.rhul.interdataitem;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {

    private static final String SECRET_KEY = "secret_1";

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals("/path")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String text = dataMap.getString(SECRET_KEY);
                    String text2 = dataMap.getString("secret_2");
                    Log.i("Leak_1",text);
                    Log.i("Leak_2", text2);
                }
            }
        }
    }
}
