package com.rhul.dataItem1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * @Testcase_name DataItems API - Mobile app
 * @author Marcos Tileria   <mtileria@gmail.com>
 * @desciption  the handheld update a DataItem. The wearable listen for changes
 * and then update a second DataItem back to the mobile. The sink could be in the
 * mobile or in the wear app.
 * mobile -> wear -> mobile  pattern using DataItems
 */
public class MainActivity extends Activity {

    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final String DEVICE_KEY = "secret";
    protected Handler myHandler;
    private DataClient dataClient;
    private TextView textView;
    private String imeiNo;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        dataClient = Wearable.getDataClient(this);


        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle msgData = msg.getData();

                if (msgData.getString("text_view")!= null){
                    String newInfo = msgData.getString("text_view");
                    textView.append("\n" + newInfo);
                }
                return true;
            }
        });

    }

    public void synClick(View v){
        getSensitiveData();
        updateDataItem();
    }

    private void getSensitiveData() {

        int statePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (statePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        }
        TelephonyManager TM = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imeiNo = TM.getImei();
    }


    public void updateDataItem(){

        imeiNo = imeiNo+" ";
        dataClient = Wearable.getDataClient(this);
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/sync");
        putDataMapReq.getDataMap().putString(DEVICE_KEY, imeiNo);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();

        Task<DataItem> putDataTask = dataClient.putDataItem(putDataReq);

        putDataTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        updateUI("sending " + imeiNo);
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




}
