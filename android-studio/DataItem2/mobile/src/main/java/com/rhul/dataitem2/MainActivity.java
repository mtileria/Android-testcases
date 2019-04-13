package com.rhul.dataitem2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

/**
 * @author Marcos Tileria <mtileria@gmail.com>
 * @Testcase_name DataItem2
 * @description This test case uses the old GoogleApiClient for DataItems and Messages
 * The mobile app modify a dataItem. The wearable app listen for changes and then
 * send back the value to the handheld via the Message Api
 */
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

        getSensitiveInformation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Wearable.MessageApi.addListener(googleClient, this);
        String PATH = "/my_path";
        Log.d(TAG, "On connected");
        DataMap dataMap = new DataMap();
        dataMap.putLong("time", new Date().getTime());
        dataMap.putString("deviceID", imei + " ");
        //Requires a new thread to avoid blocking the UI
        new SendDataThread(PATH, dataMap).start();

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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != googleClient && googleClient.isConnected()) {
            Wearable.MessageApi.removeListener(googleClient, this);
            googleClient.disconnect();

        }

    }

    private void getSensitiveInformation() {
        int statePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = TM.getImei();

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        byte[] msgBytes = messageEvent.getData();
        String text = new String(msgBytes);
        writeToLog (text); // sink

    }

    private void writeToLog(String text) {
        Log.d("mobile-sink", text);
    }

    class SendDataThread extends Thread {
        String path;
        DataMap dataMap;

        SendDataThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            // Construct a DataRequest and send over the data layer
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
            dataMapRequest.getDataMap().putAll(dataMap);
            PutDataRequest request = dataMapRequest.asPutDataRequest();
            request.setUrgent();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();
            if (result.getStatus().isSuccess()) {
                Log.d(TAG, "DataMap: " + dataMap + " sent successfully to data layer ");
            } else {
                // Log an error
                Log.d(TAG, "ERROR: failed to send DataMap to data layer");
            }
        }
    }


}