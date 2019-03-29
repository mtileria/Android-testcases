package com.yduf149.messages;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;
import android.view.View;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.Node;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity {

    private static final String TAG = "wear-app";
    Button talkButton;
    int sentMessageNumber = 1;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, ",on create");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        talkButton = findViewById(R.id.talkClick);

        talkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String onClickMessage = "I just sent the handheld a message "
                        + sentMessageNumber++;
                textView.setText(onClickMessage);
                String dataPath = "/my_path";
                new SendMessage(dataPath, "SECRET").start();
            }
        });

        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, ",BC receive in wear from service");
            if (intent.getStringExtra("message") != null){
                String messageReceived = "Message received from wear service";
                Log.d(TAG, ",BC message");
                textView.setText(messageReceived + intent.getStringExtra("message"));
            }
            else if (intent.getStringExtra("dataItem") != null){
                Log.d(TAG, ",BC dItem received");
                textView.setText(intent.getStringExtra("dataItem"));
            }
        }
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
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).
                                    sendMessage(node.getId(), path, message.getBytes());
                    try {
                        Integer result = Tasks.await(sendMessageTask);
                    } catch (ExecutionException exception) {

                    } catch (InterruptedException exception) {
                    }
                }

            } catch (ExecutionException exception) {

            } catch (InterruptedException exception) {

            }
        }
    }
}