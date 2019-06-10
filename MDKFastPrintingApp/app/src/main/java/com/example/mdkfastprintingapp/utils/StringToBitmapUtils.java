package com.example.mdkfastprintingapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 类说明：Bitmap与String相互转换工具类
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/04/11 13:43
 */
public class StringToBitmapUtils {

    public StringToBitmapUtils() {
    }

    /**
     * Bitmap --> String
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap){
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }


    /**
     * String -->Bitmap
     * @param string Base64 data
     * @return
     */
    public static Bitmap stringToBitmap(String string){
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string,Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将bitmap存入本地
     * @param bitmap
     * @return 返回的地址
     */
    public static String saveBitmapFile(Bitmap bitmap){
        String path = Environment.getExternalStorageDirectory()+"/DCIM/Camera/myPhoto.jpg";
        File file=new File(path);//将要保存图片的路径
        try{
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 传入地址获取bitmap(缩小2倍后的)
     * @param file
     * @return
     */
    public static Bitmap getBitmapFile(String file){
       return BitmapFactory.decodeFile(file,getBitmapOption(2)); //将图片的长和宽缩小味原来的1/2
    }

    /**
     * 缩小位图
     * @param inSampleSize
     * @return
     */
    private static BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }
}
