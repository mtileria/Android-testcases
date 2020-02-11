package com.rhul.simpledataitem3;



import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {

   @Override
   public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
       for (DataEvent event : dataEventBuffer) {
           if (event.getType() == DataEvent.TYPE_CHANGED) {
               // DataItem changed
               DataItem item = event.getDataItem();
               if (item.getUri().getPath().compareTo("/sync_1") == 0) {
                   DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                   Log.i("leak-string",dataMap.getString("secret"));
               } else if (item.getUri().getPath().compareTo("/sync_2") == 0) {
                  DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                  Log.i("leak-string",dataMap.getString("secret_2"));
              }
           }
       }
   }


}
