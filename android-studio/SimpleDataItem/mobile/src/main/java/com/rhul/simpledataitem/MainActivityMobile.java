package com.rhul.simpledataitem;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivityMobile extends Activity {

    private static final int REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String deviceID =  getSensitiveData();
        String path = "/" + "sync";
        String key = getKey();

        DataClient dataClient = Wearable.getDataClient(this);
        PutDataMapRequest req = PutDataMapRequest.create(path);
        req.getDataMap().putString(key, deviceID);
        req.getDataMap().putInt("number", 1);
        PutDataRequest putDataReq = req.asPutDataRequest();
        putDataReq.setUrgent();
        Task<DataItem> putDataTask = dataClient.putDataItem(putDataReq);
      //  synchronizedData(deviceID, path, key);
    }

    private String getKey(){
        return "secret";
    }

    private void synchronizedData(String text, String var, String key) {
        DataClient dataClient = Wearable.getDataClient(this);
        Integer number = 1;
        PutDataMapRequest req = PutDataMapRequest.create(var);
        req.getDataMap().putString(key, text);
        req.getDataMap().putInt("number", number);
        PutDataRequest putDataReq = req.asPutDataRequest();
        putDataReq.setUrgent();
        Task<DataItem> putDataTask = dataClient.putDataItem(putDataReq);
        if(putDataTask.isSuccessful()){
            Log.i("success","DataItem sent successfully");
        }
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
        return TM.getImei();
    }


}
