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


public class ListenerService extends WearableListenerService {

    private static final String PATH_DATA = "/sync";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final ArrayList<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        for (DataEvent event : events)
        {
            PutDataMapRequest putDataMapRequest =
                    PutDataMapRequest.createFromDataMapItem(DataMapItem.fromDataItem
                            (event.getDataItem()));

            String path = event.getDataItem().getUri().getPath();
            if (event.getType() == DataEvent.TYPE_CHANGED)
            {
                if (PATH_DATA.equals(path))
                {
                    DataMap dataMap = putDataMapRequest.getDataMap();
                    String data = dataMap.getString("data");
                    Log.i("Leak",data);
                }

            }
    }
}
}
