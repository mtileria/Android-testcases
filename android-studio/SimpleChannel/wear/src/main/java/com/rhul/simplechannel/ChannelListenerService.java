package com.rhul.simplechannel;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.wearable.ChannelClient;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChannelListenerService extends WearableListenerService {



    @Override
    public void onChannelOpened(ChannelClient.Channel channel) {
        String pathName = "/sdcard/file.txt";
        if (channel.getPath().equals("/my_path")) {
            File file = new File(pathName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri uri = Uri.fromFile(file);
            Wearable.getChannelClient(this).receiveFile(channel,uri,true);
            Path path = Paths.get(uri.getPath());

            try {

                byte[] data = Files.readAllBytes(path);
                Log.i("Leak",data.toString());

            } catch (IOException e) {
                // exception handling
            }
        }
    }
}
