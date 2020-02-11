package com.rhul.simpledataitem3;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

public class MainActivityWear extends WearableActivity {

    private static final String COUNT_KEY = "secret";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

}
