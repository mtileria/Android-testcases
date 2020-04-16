package com.rhul.dataitem2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener{

    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final String TAG = "mobile";

    GoogleApiClient googleClient;
    String imei;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Build a new GoogleApiClient
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        imei = getSensitiveInformation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Wearable.MessageApi.addListener(googleClient, this);
        //Requires a new thread to avoid blocking the UI
        synchronizedItems();

    }

    private void synchronizedItems() {
        String path = "/my_path";
        DataMap dataMap = new DataMap();
        dataMap.putString("deviceID", imei);
        new SendDataThread(path, dataMap).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != googleClient && googleClient.isConnected()) {
            Wearable.MessageApi.removeListener(googleClient, this);
            googleClient.disconnect();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Wearable.MessageApi.addListener(googleClient, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != googleClient && googleClient.isConnected()) {
            Wearable.MessageApi.removeListener(googleClient, this);
            googleClient.disconnect();

        }

    }

    private String getSensitiveInformation() {
        int statePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return TM.getImei();

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        byte[] msgBytes = messageEvent.getData();
        String text = new String(msgBytes);
        Log.i ("reply", text); // sink

    }

    class SendDataThread extends Thread {
        String path;
        DataMap dataMap;

        SendDataThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
            dataMapRequest.getDataMap().putAll(dataMap);
            PutDataRequest request = dataMapRequest.asPutDataRequest();
            request.setUrgent();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();
            Log.d(TAG, "DataMap: " + dataMap + " sent successfully to data layer ");

        }
    }


}
