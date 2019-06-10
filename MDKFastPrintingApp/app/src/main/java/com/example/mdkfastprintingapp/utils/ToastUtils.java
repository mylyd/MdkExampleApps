package com.example.mdkfastprintingapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 类说明：Toast单例工具类
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/05/15
 */
public class ToastUtils {
    private static Toast toast = null;
    public static void showToast(Context context,String message){
        if (toast == null){
            toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
            toast.show();
        }else {
            toast.setText(message);
            toast.show();
        }
    }
}
