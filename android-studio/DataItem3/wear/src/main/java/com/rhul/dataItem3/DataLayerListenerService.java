package com.rhul.dataItem3;


import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

/**
 * Listen for changes in DataItems from the mobile app with the
 * path /sync (defined in the Manifest)
 * Send a broadcast to the MainActivity to inform changes on DataItems
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String PATH_DATA = "/sync";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final ArrayList<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        for (DataEvent event : events)
        {
            PutDataMapRequest putDataMapRequest =
                    PutDataMapRequest.createFromDataMapItem(DataMapItem.fromDataItem
                            (event.getDataItem()));

            if (event.getType() == DataEvent.TYPE_CHANGED)
            {
                if (PATH_DATA.equals(event.getDataItem().getUri().getPath()))
                {
                    DataMap dataMap = putDataMapRequest.getDataMap();
                    String data = dataMap.getString("secret");
                    Log.i("Leak",data);
                }

            }
    }
}
}
