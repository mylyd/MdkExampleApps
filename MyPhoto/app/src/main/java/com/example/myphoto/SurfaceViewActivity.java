package com.example.myphoto;

import android.Manifest;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huada.publiciddevice.publicSecurityIDCardLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SurfaceViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CAMERA = 0x01;
    private static final String TAG = "SurfaceViewActivity";

    private CameraSurfaceView mCameraSurfaceView;
    private Button mBtnTake;
    private Button mBtnSwitch;
    private int mOrientation;
    // CameraSurfaceView 容器包装类
    private FrameLayout mAspectLayout;
    private boolean mCameraRequested;
    private ImageView iv_ph;
    private DrawerLayout drawerLayout;
    private TextView tv_record;
    private ProgressDialog progressDialog;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_surface_view);
        // Android 6.0相机动态权限检查
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initView();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, REQUEST_CAMERA);
        }


    }

    private void initView() {
        mAspectLayout = (FrameLayout) findViewById(R.id.layout_aspect);
        mCameraSurfaceView = new CameraSurfaceView(this);
        mAspectLayout.addView(mCameraSurfaceView);//添加视图
        mOrientation = CameraUtils.calculateCameraPreviewOrientation(SurfaceViewActivity.this);
        mBtnTake = (Button) findViewById(R.id.btn_take);
        mBtnTake.setOnClickListener(this);
        mBtnSwitch = (Button) findViewById(R.id.btn_switch);
        mBtnSwitch.setOnClickListener(this);
        iv_ph = findViewById(R.id.image_photo);
        drawerLayout = findViewById(R.id.layout_drawer);
        tv_record = findViewById(R.id.tv_record);
        tv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_take: //拍照
                takePicture();
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("正在对比...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                break;

            case R.id.btn_switch: //切换
                switchCamera();
                //ReadInformation();

                break;

            case R.id.tv_record:
                drawerLayout.openDrawer(Gravity.RIGHT);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // 相机权限
            case REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraRequested = true;
                    initView();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraUtils.stopPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraRequested) {
            CameraUtils.startPreview();
        }
    }

    /**
     * 切换相机
     * */
    private void switchCamera() {
        if (mCameraSurfaceView != null) {
            CameraUtils.switchCamera(1 - CameraUtils.getCameraID(), mCameraSurfaceView.getHolder());
            // 切换相机后需要重新计算旋转角度
            mOrientation = CameraUtils.calculateCameraPreviewOrientation(SurfaceViewActivity.this);
        }
    }

    /**
    * 拍照
    * */
    private void takePicture() {
        CameraUtils.takePicture(new android.hardware.Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, android.hardware.Camera camera) {
                CameraUtils.startPreview();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    bitmap = ImageUtils.getRotatedBitmap(bitmap, -mOrientation);
                    iv_ph.setImageBitmap(bitmap);
                    //添加两张图片发送请求，进行人脸对比 ，@ 原始照片&&实时照片，顺序不影响
                    addDataJson(bitmapToString(bitmap),
                            bitmapToString(BitmapFactory.decodeResource(getResources(),R.drawable.jpgimg)));
            }
                CameraUtils.startPreview();

            }
        });
    }

    private Bitmap decodeSampledBitmapFromURL(Uri uri, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null,options);
            options.inSampleSize = MainActivity.calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bitmapToString(Bitmap bitmap){
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }

    /**
     * @param img1 证件photo
     * @param img2 实体photo
     */
    public void addDataJson(String img1, String img2){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("img1",img1);
            jsonObject.put("img2",img2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpContrastFace(jsonObject.toString());
    }

    private publicSecurityIDCardLib iDCardDevice;
    private String pkName;
    private String toastString = "";
    private void ReadInformation() {
        try {
            if (iDCardDevice == null) {
                iDCardDevice = new publicSecurityIDCardLib(this);
            }
            int ret1 = iDCardDevice.getSAMStatus();
            int ret2 = iDCardDevice.ID_Authenticate(false);
            int ret3 = iDCardDevice.ID_ReadCardBaseMsg();
            if (ret1 == 0x90 && ret2 == 0x90 && ret3 == 0x90) {
                //内地居民身份证
                //加载身份证头像信息
                int[] colors = iDCardDevice.convertByteToColor(iDCardDevice.getBmpfileData(pkName));
                Bitmap bm = Bitmap.createBitmap(colors, 102, 126, Bitmap.Config.ARGB_8888);
                //这里你可以自定义它的大小
                Bitmap bm1 = Bitmap.createScaledBitmap(bm, (int) (125), (int) (175), false);
                if (bm1 != null) {
                    iv_ph.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bm1, null,null));
                    //iv_ph.setImageBitmap(decodeSampledBitmapFromURL(uri,iv_ph.getWidth(),iv_ph.getHeight()));
                    Log.d(TAG, "ReadInformation: bitmap != null ");
                    toastString = "ReadInformation bitmap != null ,照片已显示";
                }else {
                    Log.d(TAG, "ReadInformation: bitmap == null ");
                    toastString = "ReadInformation bitmap == null";
                }
            } else {
                if (ret1 != 0x90) {
                    toastString = "ret != 0x90 身份证阅读器连接有误，请检查设备连接状态···";
                    Log.d(TAG, "postUsbData:  ret != 0x90 身份证阅读器连接有误，请检查设备连接状态···");
                } else {
                    toastString = "else == 0x90 未检测出有身份证贴入阅读器···";
                    Log.d(TAG, "postUsbData:  else == 0x90 未检测出有身份证贴入阅读器···");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "ReadInformation: 身份证阅读器连接有误，请检查设备连接状态···");
            Log.d(TAG, "ReadInformation: 扑捉异常");
            toastString = "ReadInformation 身份证阅读器连接有误，请检查设备连接状态···（异常捕捉）";
        }
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
    }


    private void httpContrastFace(String toString) {
        final String url = "http://192.168.1.105:8081/";
        final String TYPE_HTTP = "application/json; charset=utf-8";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpContent httpContent = retrofit.create(HttpContent.class);
        RequestBody body = RequestBody.create(MediaType.parse(TYPE_HTTP),toString);
        Call<ResponseBody> bodyCall = httpContent.contrastface(body);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d(TAG, "onResponse: 请求成功~~~");
                    final String string = response.body().string();
                    if (string == null){
                        Log.d(TAG, "onResponse: String == null");
                    }else {
                        Log.d(TAG, "onResponse: String != null");
                        Log.d(TAG, "onResponse: String = "+string);
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              String toastString = "";
                              if (string.equals("success")){
                                  toastString = "对比成功";
                              }else if (string.equals("fail")){
                                  toastString = "对比失败";
                              }else if (string.equals("thephotoisnull")){
                                  toastString = "拍照失败";
                              }else if (string.equals("unknowerr")){
                                  toastString = "未知错误";
                              }
                              Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
                          }
                      });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onResponse: 请求成功，解析异常~~~");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure: 请求失败~~~~");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
}
