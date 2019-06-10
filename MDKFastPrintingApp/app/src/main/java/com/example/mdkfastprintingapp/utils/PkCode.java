package com.example.mdkfastprintingapp.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;

/**
 * 类说明：二维码/条形码生成工具类
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/04/10 14:50
 */
public class PkCode {

    public PkCode() {
    }


    /**
     * @param index 生成code类型
     * @param imageView 显示code对应的View
     * @param str 填充code的数据
     * @return
     */
    public static Bitmap generateCode(int index, ImageView imageView,String str){
        try {
            BitMatrix matrix = null;
            Bitmap bitmapImgQR = null;
            HashMap<EncodeHintType,Object> hashMap = new HashMap<>();
            hashMap.put(EncodeHintType.CHARACTER_SET,"utf-8");
            hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            MultiFormatWriter writer = new MultiFormatWriter();
            //生成条形码，BarcodeFormat.CODABAR  只支持数字
            //生成二维码，BarcodeFormat.QR_CODE
            if (index == MDK_String.CODE_QR){
                matrix = writer.encode(str, BarcodeFormat.QR_CODE, 400, 400,hashMap);
            }else if (index == MDK_String.CODE_BAR){
                matrix = writer.encode(str,BarcodeFormat.CODE_128, 200, 40,hashMap);
            }else {
                return null;
            }
            matrix = delectBitMap(matrix);//去除白边；
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width*height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x,y)){
                        pixels[y*width+x] = Color.BLACK;//生成二维码块颜色
                    }else {
                        pixels[y*width+x] = Color.WHITE;
                    }
                }
            }
            bitmapImgQR = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            bitmapImgQR.setPixels(pixels,0,width,0,0,width,height);
            return bitmapImgQR;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param matrix 消除code白边
     * @return
     */
    private static BitMatrix delectBitMap(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle(); // 获取二维码图案的属性
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) { // 循环，将二维码图案绘制到新的bitMatrix中
            for (int j = 0; j < resHeight ; j++) {
                if (matrix.get(i  + rec[0], j  + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }
}
