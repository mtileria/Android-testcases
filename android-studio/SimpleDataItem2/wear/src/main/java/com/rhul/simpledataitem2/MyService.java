package com.rhul.simpledataitem2;



import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {
//
//    @Override
//    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
//        for (DataEvent event : dataEventBuffer) {
//            if (event.getType() == DataEvent.TYPE_CHANGED) {
//                // DataItem changed
//                DataItem item = event.getDataItem();
//                if (item.getUri().getPath().compareTo("/sync") == 0) {
//                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
//                    Log.i("leak-string",dataMap.getString("secret"));
//                }
//                if (item.getUri().getPath().compareTo("/sync") == 0) {
//                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
//                    Integer value = dataMap.getInt("normal_int");
//                    Log.i("leak-int", value.toString());
//                }
//            }
//        }
//    }


}
