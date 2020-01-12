package com.rhul.simplemessageold;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private TextView mTextView;
    GoogleApiClient googleClient;

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
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.MessageApi.addListener(googleClient, this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase("/sync")){
            byte[] msgBytes = messageEvent.getData();
            String text = new String(msgBytes);
            Log.i("Leak",text);
        }
    }

    @Override
    protected void onStop(){
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
    protected void onDestroy() {
        super.onDestroy();
        if (null != googleClient && googleClient.isConnected()) {
            Wearable.MessageApi.removeListener(googleClient, this);
            googleClient.disconnect();

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
