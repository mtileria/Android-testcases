package com.rhul.simpledataitem2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivityMobile2 extends Activity  {

    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final String DEVICE_KEY = "secret";
    private DataClient dataClient;
    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataClient = Wearable.getDataClient(this);
        deviceID =  getSensitiveData();
        synchronizedData(deviceID);
    }

    private void synchronizedData(String text) {
        text = text+" ";
        DataMap dataMap = new DataMap();
        dataMap.putString(DEVICE_KEY, text);
        dataMap.putInt("integer",1);
        PutDataMapRequest req = PutDataMapRequest.create("/sync");
        req.getDataMap().putAll(dataMap);
        PutDataRequest putDataReq = req.asPutDataRequest();
        putDataReq.setUrgent();
        Task<DataItem> putDataTask = dataClient.putDataItem(putDataReq);
    }


    private String getSensitiveData() {

        int statePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        }
        TelephonyManager TM = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return deviceID = TM.getImei();
    }


}
