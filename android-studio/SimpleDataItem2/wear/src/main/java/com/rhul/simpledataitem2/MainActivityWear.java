package com.rhul.simpledataitem2;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class MainActivityWear extends WearableActivity implements DataClient.OnDataChangedListener {

    private static final String COUNT_KEY = "secret";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/sync") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Log.i("leak-string",dataMap.getString(COUNT_KEY));
                }
                if (item.getUri().getPath().compareTo("/sync") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Integer value = dataMap.getInt("normal_int");
                    Log.i("leak-int", value.toString());
                }
            }
        }
    }
}
