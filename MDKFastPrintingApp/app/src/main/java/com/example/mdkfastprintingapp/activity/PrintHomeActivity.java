package com.example.mdkfastprintingapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mdkfastprintingapp.MainActivity;
import com.example.mdkfastprintingapp.R;
import com.example.mdkfastprintingapp.camera.ImageUtils;
import com.example.mdkfastprintingapp.dialog.DialogHintActivity;
import com.example.mdkfastprintingapp.utils.MDK_String;
import com.example.mdkfastprintingapp.utils.ParameterUtils;
import com.example.mdkfastprintingapp.utils.PkCode;
import com.example.mdkfastprintingapp.utils.StringToBitmapUtils;
import com.example.mdkfastprintingapp.utils.ToastUtils;
import com.example.mdkfastprintingapp.utils.logs;
import com.example.mdkfastprintingapp.voice.VoicePromptUtils;
import com.example.mylibrary.SUP300_API;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 类说明：打印证件--身份证认证-- @打印证件
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/04/19 11:27
 */
public class PrintHomeActivity extends AppCompatActivity {
    private String TAG = "PrintHomeActivity red log";

    @BindView(R.id.show_img)
    ImageView showImg;
    @BindView(R.id.tv_Title)
    TextView tvTitle;
    @BindView(R.id.img_Photo)
    ImageView imgPhoto;
    @BindView(R.id.tv_Name)
    TextView tvName;
    @BindView(R.id.tv_Age)
    TextView tvAge;
    @BindView(R.id.tv_Sex)
    TextView tvSex;
    @BindView(R.id.tv_Examination)
    TextView tvExamination;
    @BindView(R.id.tv_Time)
    TextView tvTime;
    @BindView(R.id.img_Yard)
    ImageView imgYard;
    @BindView(R.id.tv_NumberID)
    TextView tvNumberID;
    @BindView(R.id.tv_Site)
    TextView tvSite;
    @BindView(R.id.img_Chapter)
    ImageView imgChapter;
    @BindView(R.id.view_RelativeLayout)
    RelativeLayout viewRelativeLayout;
    @BindView(R.id.btn_PrintCard)
    Button btnPrintCard;
    private String str_CardId;
    List<String> mlist = new ArrayList<>();
    Bitmap bitmap = null;
    private DialogHintActivity dialogHintActivity;
    //
    private SUP300_API mu_sup300 = new SUP300_API();
    private UsbManager mUsbManager;
    private long retValue;
    private static UsbDevice mUsbDevice;
    private Timer timer;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printhome);
        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        str_CardId = bundle.getString("IDNo");
        dialogHintActivity = new DialogHintActivity("正在加载，请稍等....");
        dialogHintActivity.setOnDialogHintText(new DialogHintActivity.OnDialogHintText() {
            @Override
            public void OnChooseDialog() {
                startActivity(new Intent(PrintHomeActivity.this, MainActivity.class));
            }
        });
        dialogHintActivity.show(getSupportFragmentManager(), "hitDialog");

        HttpCardInforMation(str_CardId);
        sharedPreferences = getSharedPreferences("template", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progressDialog = new ProgressDialog(PrintHomeActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (mUsbManager == null){
            logs.d(TAG, "onCreate: usb设备服务未启动");
            ToastUtils.showToast(this,"usb设备服务未正常启动...");
            finish();
            return;
        }else {
            logs.d(TAG, "onCreate: usb设备-->"+String.valueOf(mUsbManager.toString()));
        }
        mUsbDevice = mu_sup300.SUP300_OPEN(mUsbManager);
        retValue = mu_sup300.SUP300_INIT(mUsbManager,mUsbDevice);
        logs.d(TAG, "onCreate: this is cardId post:"+str_CardId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null){
            timer.cancel();
        }
        finish();
    }

    /**
     * 请求健康证信息
     * @param sInfo card id
     */
    private void HttpCardInforMation(final String sInfo) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MDK_String.URL_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ParameterUtils parameterUtils = retrofit.create(ParameterUtils.class);
        Call<ResponseBody> dataInfo = parameterUtils.postCardID(sInfo);
        dataInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                logs.d(TAG, "onResponse: 请求成功");
                try {
                    final String string = response.body().string();
                    logs.d(TAG,string);
                    if (!TextUtils.isEmpty(string)) {
                        JSONObject jsonObject = new JSONObject(string);
                        if (jsonObject.getString(MDK_String.JSON_DATA) != null) {
                            JSONObject object = new JSONObject(jsonObject.getString(MDK_String.JSON_DATA));
                            mlist.add(object.getString(MDK_String.JSON_NAME));
                            mlist.add(object.getString(MDK_String.JSON_AGE));
                            mlist.add(object.getString(MDK_String.JSON_GENDER));
                            mlist.add(object.getString(MDK_String.JSON_MEDICAL));
                            mlist.add(object.getString(MDK_String.JSON_HEALTHMUM));
                            mlist.add(object.getString(MDK_String.JSON_HEALTHTIME));
                            mlist.add(object.getString(MDK_String.JSON_QRCARD));
                            mlist.add(object.getString(MDK_String.JSON_ADDRESS));
                            mlist.add(object.getString(MDK_String.JSON_GZ));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogHintActivity.dismiss();
                                    if (mlist.get(3).equals("合格")){
                                        setShowUiData();
                                    }else {
                                        ToastUtils.showToast(PrintHomeActivity.this,"体检状态不合格，无法打印...");
                                        VoicePromptUtils.onPlay(PrintHomeActivity.this,MDK_String.MUSIC_9);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            logs.d(TAG, "onResponse: JSON_STATUS  == null ");
                        }
                    } else {
                        logs.d(TAG, "onResponse: log--string == null ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    logs.d(TAG, "onResponse: 222222异常");
                } catch (JSONException e) {
                    e.printStackTrace();
                    logs.d(TAG, "onResponse: 111111异常");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           ToastUtils.showToast(PrintHomeActivity.this,"暂无查询到您的个人信息，请录入信息后查询打印...");
                           VoicePromptUtils.onPlay(PrintHomeActivity.this,MDK_String.MUSIC_10);
                           finish();
                        }
                    });
                }catch (NullPointerException e){
                    e.printStackTrace();
                    logs.d(TAG,"response.body().string() = null ");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                logs.d(TAG, "onResponse: 请求失败");
            }
        });
    }

    private void getHttpTemplate(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MDK_String.URL_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ParameterUtils parameterUtils = retrofit.create(ParameterUtils.class);
        Call<ResponseBody> dataInfo = parameterUtils.getTemplate();
        dataInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //处理返回模板逻辑
                //getInt("tem",0)
                //将返回的数据 分为 type = 1 || 2 || 3 ,分别对应 无版 有章 无章  存入即可editor.putInt("tem",type);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * 填充数据，显示健康证页面
     */
    private void setShowUiData() {
        String info = "";
        tvTitle.setText("健       康       证");
        tvName.setText(getString(R.string.card_name) + mlist.get(0));
        tvAge.setText(getString(R.string.card_age) + mlist.get(1));
        tvSex.setText(getString(R.string.card_gender) + mlist.get(2));
        tvExamination.setText(getString(R.string.card_medical) + mlist.get(3));
        tvNumberID.setText(getString(R.string.card_idnumber)+mlist.get(4));
        tvTime.setText(getString(R.string.card_health_time) + mlist.get(5));
        imgPhoto.setImageBitmap(StringToBitmapUtils.stringToBitmap(mlist.get(6)));
        tvSite.setText(mlist.get(7));
        imgChapter.setImageBitmap(StringToBitmapUtils.stringToBitmap(mlist.get(8)));
        imgYard.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //更改码类型
        info = mlist.get(4)+";"+mlist.get(3)+";\n"+
                setString_int(str_CardId,0,6)+"********"+setString_int(str_CardId,14,18)+";\n"+ mlist.get(5);

        imgYard.setImageBitmap(PkCode.generateCode(MDK_String.CODE_QR, imgYard, info));
        viewRelativeLayout.setBackgroundResource(R.color.colorWhite);
        //viewRelativeLayout.invalidate();

    }


    /**
     * 截图功能
     *
     * @param views  需要截取的View
     * @param bitmap 一个空实例对象bitmap
     * @return
     */
    Bitmap showTopo(View views, Bitmap bitmap) {
        View shotView = getWindow().getDecorView();
        shotView.setDrawingCacheEnabled(true);
        shotView.buildDrawingCache();
        bitmap = shotView.getDrawingCache();
        int outWidth = views.getWidth();
        int outHeight = views.getHeight();
        int[] viewLocationArray = new int[2];
        views.getLocationOnScreen(viewLocationArray);
        bitmap = Bitmap.createBitmap(bitmap, viewLocationArray[0], viewLocationArray[1], outWidth, outHeight);
        return bitmap;
    }

    /**
     * 截取字符串
     * @param arg 需要截取的字符串
     * @param ins1 开始位
     * @param ins2 结束位
     * @return
     */
            private int setString_int(String arg,int ins1,int ins2) {
        String str = arg.substring(ins1, ins2);
        try {
            int arga = Integer.parseInt(str);
            return arga;
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return 0;
    }

    /**
     * 打印点击按钮
     * @param view
     */
    @OnClick({ R.id.btn_PrintCard})
    public void onClick(View view) {
        timer = new Timer();
        timer.schedule(task,0,1000);
        onTemplate(sharedPreferences.getInt("tem",1));
        showImg.setImageBitmap(bitmap);
        bitmap = ImageUtils.getRotatedBitmap(bitmap,-90);
        runnable.run();
    }


    private void onTemplate(int type){
        if (type == 2){
            //有章版
            tvTitle.setVisibility(View.GONE);
            tvSite.setVisibility(View.GONE);
        }else if (type == 3){
            //无章版
            tvTitle.setVisibility(View.GONE);
            tvSite.setVisibility(View.GONE);
            imgChapter.setVisibility(View.GONE);
        }

        if (bitmap == null){
            bitmap = showTopo(viewRelativeLayout, bitmap);
        }else {
            bitmap = null;
            bitmap = showTopo(viewRelativeLayout,bitmap);
        }
    }

    int endTime = 38;//s
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            endTime--;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage("正在打印（"+endTime+"s）....");
                    progressDialog.show();
                    if (endTime == 0){
                        finish();
                    }
                }
            });
        }
    };

    /**
     * 打印健康证
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        mUsbDevice= mu_sup300.SUP300_OPEN(mUsbManager);
                        retValue = mu_sup300.SUP300_INIT(mUsbManager,mUsbDevice);
                        if (retValue != 0x00 || mUsbDevice == null){
                            finish();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast(PrintHomeActivity.this,"打印机连接错误，请检查设备的连接状态...");
                                }
                            });
                        }
                        retValue = mu_sup300.SUP300_ClearImageBuffer(mUsbManager,mUsbDevice);//清除缓存
                        retValue = mu_sup300.SUP300_HopperCardLoading(mUsbManager,mUsbDevice);//进卡
                        retValue = mu_sup300.SUP300_SetRibbonType(mUsbManager,mUsbDevice);//设置色带

                        Thread.sleep(3000);//休眠2秒
                        retValue = mu_sup300.SUP300_ImageDownloadforPrinting(mUsbManager,mUsbDevice,bitmap); //加载图片资源
                        Thread.sleep(5000);//休眠2秒
                        retValue = mu_sup300.SUP300_DoPrintBuffer(mUsbManager,mUsbDevice);//打印
                        Thread.sleep(27*1000);
                        retValue = mu_sup300.SUP300_EjectToStacker(mUsbManager,mUsbDevice);
                           // thread.stop();
                        bitmap = null;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
