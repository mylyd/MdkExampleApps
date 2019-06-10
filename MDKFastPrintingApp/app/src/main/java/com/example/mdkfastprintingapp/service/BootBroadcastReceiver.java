package com.example.mdkfastprintingapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mdkfastprintingapp.MainActivity;
import com.example.mdkfastprintingapp.activity.ActivateLoginActivity;
import com.example.mdkfastprintingapp.utils.logs;

/**
 * 类说明：开机自启--实现广播
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/05/08
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver log";
    private static String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            Intent mainActivityIntent = new Intent(context, ActivateLoginActivity.class);
            logs.d(TAG,"this zhe shi di yi ge qi dong de BootBroadcastActivity");
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }
}
