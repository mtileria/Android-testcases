package com.rhul.interdataitem2;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity {

    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final String KEY_ID = "secret_1";
    private DataClient dataClient;
    private String deviceID;
    private DataMap dataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataClient = Wearable.getDataClient(this);
        deviceID = source();
        dataMap = new DataMap();
        String key = getKey();
        dataMap.putString(key,deviceID);
        synchronizedData();

    }

    private void synchronizedData() {
        PutDataRequest putDataReq = getPutDataMapRequest();
        dataClient.putDataItem(putDataReq);

    }

    private PutDataRequest getPutDataMapRequest(){
      PutDataMapRequest putMapDataReq = getRequest(deviceID);
      return putMapDataReq.asPutDataRequest();
    }

    private PutDataMapRequest getRequest(String text){
        DataMap dataMap2 = new DataMap();
        dataMap2.putInt("five",5);
        PutDataMapRequest req = PutDataMapRequest.create("/path");
        req.getDataMap().putString(KEY_ID, text);
        req.getDataMap().putAll(dataMap2);
        req.getDataMap().putAll(dataMap);
        return req;
    }


    private String source() {

        int statePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        }
        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return deviceID = TM.getImei();
    }

    private String getKey() {
        return "secret" + "_2";
    }


}
