package com.rhul.simpledataitemold;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private TextView mTextView;
    GoogleApiClient googleClient;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final String path = "/my_path";
    private String text;
    public static String test;

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

        text = "getSensitiveInformation()";
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
      // DataMap dataMap = new DataMap();
      // dataMap.putString("deviceID", text);
      // PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
      // dataMapRequest.getDataMap().putAll(dataMap);
      // PutDataRequest request = dataMapRequest.asPutDataRequest();
      // PendingResult pResult = Wearable.DataApi.putDataItem(googleClient, request);
      new SyncDataItemTread(path,text, this).start();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class SyncDataItemTread extends Thread {
        Activity ref;
        String path;
        String id;

        SyncDataItemTread(String p, String data, Activity refParent) {
            ref = refParent;
            path = p;
            id = data;
        }
        private String getSensitiveInformation() {
            int statePermissionCheck = ContextCompat.checkSelfPermission(ref,
                    Manifest.permission.READ_PHONE_STATE);
            if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ref,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            }
            TelephonyManager mgr = (TelephonyManager) ref.getSystemService(TELEPHONY_SERVICE);
            return  mgr.getDeviceId();
        }

        public void run() {
            id = getSensitiveInformation();
            DataMap dataMap = new DataMap();
            dataMap.putString("deviceID", id);
            test = id;
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
            dataMapRequest.getDataMap().putAll(dataMap);

            PutDataRequest request = dataMapRequest.asPutDataRequest();
            PendingResult pResult = Wearable.DataApi.putDataItem(googleClient, request);
            if (pResult.await().getStatus().isSuccess()) {
                Log.i("Success", "DataMap sent to the data layer ");
            } else {
                Log.i("Error","failed to send DataMap");
            }
        }
    }
}
