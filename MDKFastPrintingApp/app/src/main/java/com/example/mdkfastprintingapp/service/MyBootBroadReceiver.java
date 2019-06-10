package com.example.mdkfastprintingapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mdkfastprintingapp.utils.logs;

public class MyBootBroadReceiver extends BroadcastReceiver {

    private static final String TAG ="MyBootBroadReceiver" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        logs.d(TAG,"启动了这个广播");

    }
}
