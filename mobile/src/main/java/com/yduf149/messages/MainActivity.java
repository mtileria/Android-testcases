package com.yduf149.messages;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mobile-app";
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final String DEVICE_KEY = "device";
    protected Handler myHandler;
    Button talkButton;
    TextView textView;
    Button secButton;
    TextView secTextView;
    int sentMessageNumber = 1;
    String imeiNo;
    int counter = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        talkButton = findViewById(R.id.talkButton);
        textView = findViewById(R.id.textView);
        secButton = findViewById(R.id.syncButton);
        secTextView = findViewById(R.id.textViewSync);


        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.d(TAG, "Create a handlerMsg");
                Bundle stuff = msg.getData();
                //messageText(stuff.getString("messageText"));
                String newInfo = stuff.getString("messageText");
                if (newInfo.compareTo("") != 0) {
                    Log.d(TAG, "newInfo");
                    textView.append("\n" + newInfo);
                }
                return true;
            }
        });

        int statePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imeiNo = TM.getImei();


        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }


    public void talkClick(View v) {
        Log.d(TAG, "On talkClick");
        textView.setText("Sending message.... ");
        new NewThread("/my_path", imeiNo,"message").start();

    }

    public void syncClick(View view) {
        Log.d(TAG, "On SynClick");
        secTextView.setText("Synchronizing item .... ");
        imeiNo = imeiNo + Integer.toString(++counter);
        new NewThread("/sync", imeiNo,"dataItem").start();
    }


    // update UI
    public void sendMessageUI(String messageText) {
        Log.d(TAG, "On sendMessageUI");
        Bundle bundle = new Bundle();
        bundle.putString("messageText", messageText);
        Message msg = myHandler.obtainMessage();
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }


    public class Receiver extends BroadcastReceiver {

        @Override
        // Upon receiving the reply from the wearable
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "On-receive");
            String message = "I just received mi Id  back from the wearable "
                    + intent.getStringExtra("message");
            textView.setText(message);
        }
    }

    class NewThread extends Thread {
        String path;
        String message;
        String type;

        NewThread(String p, String m, String t) {
            path = p;
            message = m;
            type = t;
        }

        public void run() {

            Log.d(TAG, "in-run");
            Task<List<Node>> wearableList =
                    Wearable.getNodeClient(getApplicationContext())
                            .getConnectedNodes();

            switch(type){

                case "message":
                    try {

                        List<Node> nodes = Tasks.await(wearableList);
                        for (Node node : nodes) {
                            Task<Integer> sendMessageTask = Wearable.getMessageClient(MainActivity.this)
                                    .sendMessage(node.getId(), path, message.getBytes());

                            try {
                                //Block on a task and get the result synchronously

                                Integer result = Tasks.await(sendMessageTask);
                                sendMessageUI("I just sent the wearable a message " + sentMessageNumber++);

                            } catch (Exception exception) {

                                //TO DO: Handle the exception

                            }

                        }

                    } catch (Exception exception) {

                        //TO DO: Handle the exception

                    }
                    break;

                case "dataItem":
                    Log.d(TAG,"updateDataItem");

                    PutDataMapRequest putDataMapReq = PutDataMapRequest.create(path);
                    putDataMapReq.getDataMap().putString(DEVICE_KEY, imeiNo);
                    PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                    putDataReq.setUrgent();

                    Task<DataItem> putDataTask = Wearable.getDataClient(MainActivity.this)
                            .putDataItem(putDataReq);

                    putDataTask.addOnSuccessListener(
                            new OnSuccessListener<DataItem>() {
                                @Override
                                public void onSuccess(DataItem dataItem) {
                                    Log.d(TAG, "leaking data ... " + dataItem);
                                    secTextView.setText(imeiNo);
                                }
                            });

                    break;

            }


        }
    }


}