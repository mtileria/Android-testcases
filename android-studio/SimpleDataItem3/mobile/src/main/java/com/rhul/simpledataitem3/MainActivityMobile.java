package com.rhul.simpledataitem3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivityMobile extends Activity  {

    private static final int REQUEST_READ_PHONE_STATE = 1;
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

        PutDataMapRequest req = PutDataMapRequest.create("/sync_1");
        req.getDataMap().putString("secret", text);
        PutDataRequest putDataReq = req.asPutDataRequest();
        dataClient.putDataItem(putDataReq);

        // second
        PutDataMapRequest req2 = PutDataMapRequest.create("/sync_2");
        req2.getDataMap().putString("secret_2", text + "2");
        PutDataRequest putDataReq2 = req2.asPutDataRequest();
        dataClient.putDataItem(putDataReq2);
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
