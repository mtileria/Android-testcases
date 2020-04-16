package com.rhul.messages;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.Node;
import java.util.List;


public class MainActivity extends WearableActivity {

    private TextView textView;

    private final Receiver messageReceiver = new Receiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);

        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);
    }



    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("message") != null){
                String text = "Sending id back to handheld";
                String msg = intent.getStringExtra("message");
                textView.setText(text + msg);
                sendMessageBack("/my_path",msg);
            }

        }
    }
    // send id back to the handheld
    private void sendMessageBack(String path, String msg){
        new SendMessage(path,msg).start();

    }

    class SendMessage extends Thread {
        String path;
        String message;

        SendMessage(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {

            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();

            try {
                List<Node> nodes = Tasks.await(nodeListTask);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask = Wearable.getMessageClient(MainActivity.this).
                            sendMessage(node.getId(), path, message.getBytes());
                    Tasks.await(sendMessageTask);
                }
            } catch (Exception exception) {
            }
        }
    }


}
