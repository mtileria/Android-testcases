package com.rhul.messages;

import android.Manifest;
import android.app.Activity;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;



public class MainActivity extends Activity {

    private static final int REQUEST_READ_PHONE_STATE = 1;
    protected Handler myHandler;
    Button talkButton;
    TextView textView;
    TextView textViewReply;
    String deviceID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        talkButton = findViewById(R.id.talkButton);
        textView = findViewById(R.id.textView);
        textViewReply = findViewById(R.id.textViewReply);



        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle msgData = msg.getData();

                if (msgData.getString("text_view")!= null){
                    String newInfo = msgData.getString("text_view");
                    textView.append("\n" + newInfo);

                }else if(msgData.getString("text_reply")!= null){
                    String newInfo = msgData.getString("text_reply");
                    textViewReply.append("\n" + newInfo);
                }
                return true;
            }
        });

        getSensitiveInformation();
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);


    }

    private void getSensitiveInformation(){
        int statePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceID = TM.getImei();

    }

    public void talkClick(View v) {
        textView.setText("Sending message to wearable ");
        new NewThread("/my_path", deviceID).start();

    }


    // Send a message to the Handler
    public void updateUI(String type , String messageText) {
        Bundle bundle = new Bundle();
        String t = (type.compareTo("text_view")==0) ? "text_view": "text_reply";
        bundle.putString(t, messageText);
        Message msg = myHandler.obtainMessage();
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }


    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String text = "I just received mi Id  back from the wearable:";
            String reply = intent.getStringExtra("reply");
            updateUI("text_reply",text + reply);
            writeToLog(reply);
        }
    }

    private void writeToLog(String reply) {
        Log.i("INFO",reply);
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
                            Task<Integer> sendMessageTask = Wearable.getMessageClient(MainActivity.this)
                                    .sendMessage(node.getId(), path, message.getBytes());

                                //Block on a task and get the result synchronously
                                Tasks.await(sendMessageTask);
                                updateUI("text_view",
                                        "I just sent my id to the wearable");
                                Log.i("INFO",deviceID);
                                writeToLog(message); //sink
                        }
                    } catch (Exception exception) {
                        //TO DO: Handle the exception
                    }

        }
    }


}
