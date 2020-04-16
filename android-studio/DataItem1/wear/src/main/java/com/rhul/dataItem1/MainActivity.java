package com.rhul.dataItem1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends WearableActivity {

    private TextView mTextView;
    final static String TAG = "wear-app";
    Handler myHandler;
    String sensitiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.textView);

        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver dataItemReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataItemReceiver,newFilter);

        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle msgData = msg.getData();

                if (msgData.getString("text_view")!= null){
                    String newInfo = msgData.getString("text_view");
                    mTextView.append("\n" + newInfo);
                }
                return true;
            }
        });
    }

    public class Receiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("secret");
            updateUI(data);
            synchronizedHandheld(data);  // send data back -> sync using other DataItem

        }
    }

    private void synchronizedHandheld(String data) {
        sensitiveData = data;
        DataClient dataClient = Wearable.getDataClient(this);
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/sync_wear");
        putDataMapReq.getDataMap().putString("reply", sensitiveData);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();

        Task<DataItem> putDataTask = dataClient.putDataItem(putDataReq);
        Log.d(TAG,data);
        writeToLog(data); //sink

        putDataTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        updateUI("Sync DataItem with mobile " + sensitiveData);
                    }
                });
    }

    public void updateUI(String text){
        Bundle bundle = new Bundle();
        bundle.putString("text_view", text);
        Message msg = myHandler.obtainMessage();
        msg.setData(bundle);
        myHandler.sendMessage(msg);

    }


    private void writeToLog(String reply) {
        Log.d(TAG,reply);
    }



}
