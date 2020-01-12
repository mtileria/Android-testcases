package com.rhul.dataItem3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * @Testcase_name DataItems API - Mobile app
 * @author Marcos Tileria   <mtileria@gmail.com>
 * @desciption  the handheld update a DataItem.
 * The wearable listen for changes in a Service and then update
 * a second DataItem back to the mobile.
 * The mobile write to the log in the ListenerService
 * The sink could be in the mobile or in the wear app.
 * mobile -> wear -> mobile  pattern using DataItems
 */
public class MainActivity extends Activity {

    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final String DEVICE_KEY = "secret";
    private DataClient dataClient;
    private String imeiNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imeiNo =  getSensitiveData();
        updateDataItem();
    }

    public void updateDataItem(){

        dataClient = Wearable.getDataClient(this);
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/sync");
        putDataMapReq.getDataMap().putString(DEVICE_KEY, imeiNo);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();
        Task<DataItem> result = dataClient.putDataItem(putDataReq);
        if (result.isSuccessful()) {
            return;
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
        return imeiNo = TM.getImei();
    }
}
