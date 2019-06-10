package com.example.readthecard.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 类说明：
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/0x/xx xx:xx
 */
public class ImageUtils {
    /**
     * 旋转图片
     * @param bitmap
     * @param rotation
     * @Return
     */
    public static Bitmap getRotatedBitmap(Bitmap bitmap, int rotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }

    /**
     * 镜像翻转图片
     * @param bitmap
     * @Return
     */
    public static Bitmap getFlipBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(bitmap.getWidth(), 0);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }
}
