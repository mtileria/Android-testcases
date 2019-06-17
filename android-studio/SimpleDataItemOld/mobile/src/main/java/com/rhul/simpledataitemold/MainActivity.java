package com.rhul.simpledataitemold;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private TextView mTextView;
    GoogleApiClient googleClient;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    protected final String TAG = "MessageOld-mobile";
    private static final String PATH = "/my_path";
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        id = getSensitiveInformation();
    }


    private String getSensitiveInformation() {
        int statePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
        TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        return  mgr.getDeviceId();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        super.onStop();
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        googleClient.connect();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new SyncDataItemTread(PATH,id).start();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class SyncDataItemTread extends Thread {
        String path;
        String id;

        SyncDataItemTread(String p, String data) {
            path = p;
            id = data;
        }

        public void run() {
            // Construct a DataRequest and send over the data layer
            DataMap dataMap = new DataMap();
            dataMap.putString("deviceID", id + " ");
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
            dataMapRequest.getDataMap().putAll(dataMap);
            PutDataRequest request = dataMapRequest.asPutDataRequest();
            request.setUrgent();
            PendingResult pResult = Wearable.DataApi.putDataItem(googleClient, request);
            if (pResult.await().getStatus().isSuccess()) {
                Log.i(TAG, "DataMap: " + dataMap + " sent successfully to data layer ");
            } else {
                Log.i(TAG, "ERROR: failed to send DataMap to data layer");
            }
        }
    }
}
