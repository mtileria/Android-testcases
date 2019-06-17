package com.rhul.simplemessage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_READ_PHONE_STATE = 1;
    String deviceId;
    MessageClient messageClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageClient = Wearable.getMessageClient(this);
        deviceId = source();
        createMessage(deviceId);

    }

    public String source() {


        int statePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        }
        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return TM.getImei();


    }

    public void createMessage(String text) {
        new NewThread("/my_path", text).start();

    }

    class NewThread extends Thread {
        String path;
        String message;

        NewThread(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {
            Task<List<Node>> wearableList =
                    Wearable.getNodeClient(getApplicationContext())
                            .getConnectedNodes();
            try {

                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask = messageClient
                            .sendMessage(node.getId(), path, message.getBytes());
                    Log.i("INFO", message);

                    //Block on a task and get the result synchronously
                    Tasks.await(sendMessageTask);
                }
            } catch (Exception exception) {
                //TO DO: Handle the exception
            }

        }
    }


}