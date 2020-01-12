package com.rhul.assets;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.UUID;


public class MainActivity extends WearableActivity {


    private String key = "secret_1";
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text =  source();
        sendAsset();
        sendAsset(text);
    }

    private String source() {
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }


    public void sendAsset(){

        Asset asset = Asset.createFromBytes(text.getBytes());
        PutDataRequest request = PutDataRequest.create("/path_1");
        request.putAsset(key, asset);
        Wearable.getDataClient(this).putDataItem(request);

    }

    public void sendAsset(String text){

        Asset asset = Asset.createFromBytes(text.getBytes());
        PutDataMapRequest pdmRequest = PutDataMapRequest.create("/path_2");
        pdmRequest.getDataMap().putAsset("secret_2", asset);
        PutDataRequest request = pdmRequest.asPutDataRequest();
        Wearable.getDataClient(this).putDataItem(request);

    }


}
