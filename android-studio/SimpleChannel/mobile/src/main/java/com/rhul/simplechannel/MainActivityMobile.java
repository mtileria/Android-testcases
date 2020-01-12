package com.rhul.simplechannel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.ChannelClient;

import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivityMobile extends Activity  {

    private ChannelClient channelClient;
    private static final int REQUEST_READ_PHONE_STATE = 1;

    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        channelClient = Wearable.getChannelClient(this);

        deviceID =  getSensitiveData();

        File file = getTaintedFile(deviceID);


        Task<ChannelClient.Channel> channelTask = channelClient.openChannel("*","/my_path");
        ChannelClient.Channel channel = channelTask.getResult();
        channelClient.sendFile(channel, Uri.fromFile(file));
    }

    private File getTaintedFile(String deviceID) {
        File file = new File("filePath");

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = deviceID.getBytes();
            outputStream.write(buffer);

        }catch(Exception e){
            e.printStackTrace();
        }

        return file;
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
