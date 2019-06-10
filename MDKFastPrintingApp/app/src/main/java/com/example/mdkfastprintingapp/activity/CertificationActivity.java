package com.example.mdkfastprintingapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mdkfastprintingapp.MainActivity;
import com.example.mdkfastprintingapp.R;
import com.example.mdkfastprintingapp.bean.InforData;
import com.example.mdkfastprintingapp.dialog.DialogHintActivity;
import com.example.mdkfastprintingapp.utils.MDK_String;
import com.example.mdkfastprintingapp.utils.StringToBitmapUtils;
import com.example.mdkfastprintingapp.utils.logs;
import com.example.mdkfastprintingapp.voice.VoicePromptUtils;
import com.huada.publiciddevice.publicSecurityIDCardLib;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 类说明：打印证件--@身份证认证
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/04/19 11:27
 */

public class CertificationActivity extends AppCompatActivity {
    @BindView(R.id.tv_ctime)
    TextView tvCtime;
    @BindView(R.id.cs_img)
    ImageView csImg;
    private publicSecurityIDCardLib iDCardDevice;
    private Bitmap bm1;
    private String pkName;
    private String TAG = "CertificationActivity log";
    private Timer timer;
    private DialogHintActivity dialogHintActivity;
    int dTime = 10;
    private int activityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification);
        ButterKnife.bind(this);
        pkName = CertificationActivity.this.getPackageName();
        logs.d(TAG,"0000000000000"+pkName);
        Bundle bundle = this.getIntent().getExtras();
        activityType = bundle.getInt("ActivityType");
        VoicePromptUtils.onPlay(CertificationActivity.this, MDK_String.MUSIC_1);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        timer = new Timer();
        timer.schedule(task, 1 * 1000, 1 * 1000);
    }


    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logs.d(TAG, dTime + "");
                    if (dTime < 1) {
                        startActivity(new Intent(CertificationActivity.this, MainActivity.class));
                        finish();
                    } else {
                        tvCtime.setText(dTime + "s");
                        dTime--;
                        ReadInformation();
                    }
                }
            });
        }
    };

    /**
     * 读取身份证信息
     */
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
                bm1 = Bitmap.createScaledBitmap(bm, (int) (125), (int) (175), false);

                if (iDCardDevice.GetCardType() == 0) { //居民身份证
                    if (activityType == MDK_String.INFOR_TYPE) {
                        InforData inforData = new InforData();
                        inforData.setName(iDCardDevice.getName());
                        inforData.setSex(iDCardDevice.getSex());
                        inforData.setNation(iDCardDevice.getNation());
                        inforData.setBirth(iDCardDevice.getBirth());
                        inforData.setAddress(iDCardDevice.getAddress());
                        inforData.setIDNo(iDCardDevice.getIDNo());
                        inforData.setEffectDate(iDCardDevice.getEffectDate());
                        inforData.setExpireDate(iDCardDevice.getExpireDate());
                        inforData.setDepartment(iDCardDevice.getDepartment());
                        //inforData.setUri(StringToBitmapUtils.saveBitmapFile(bm));
                        Intent intent1 = new Intent(this, InformationActivity.class);
                        Bundle bundleInfor = new Bundle();
                        bundleInfor.putSerializable("information", inforData);
                        bundleInfor.putString("photo", StringToBitmapUtils.bitmapToString(bm1));
                        intent1.putExtras(bundleInfor);
                        startActivity(intent1);
                        //csImg.setImageBitmap(bm1);
                        logs.d(TAG, "activity type --> Information页面过来的 ");
                    } else if (activityType == MDK_String.PRINT_TYPE) {
                        //使用bundle传数据   /*数据的注入*/
                        Intent intent2 = new Intent(this, PrintHomeActivity.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("IDNo", iDCardDevice.getIDNo());
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        logs.d(TAG, "activity type --> PrintCard页面过来的");
                    }
                    finish();
                }
            } else {
                if (ret1 != 0x90) {
                    if (dialogHintActivity == null) {
                        dialogHintActivity = new DialogHintActivity("身份证阅读器连接有误，请检查设备连接状态···");
                        dialogHintActivity.setOnDialogHintText(new DialogHintActivity.OnDialogHintText() {
                            @Override
                            public void OnChooseDialog() {
                            }
                        });
                        dialogHintActivity.show(getSupportFragmentManager(), "hitDialog");
                    }
                } else {
                    logs.d(TAG, "未检测出有身份证贴入阅读器···");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (dialogHintActivity == null) {
                dialogHintActivity = new DialogHintActivity("身份证阅读器连接有误，请检查设备连接状态···");
                dialogHintActivity.setOnDialogHintText(new DialogHintActivity.OnDialogHintText() {
                    @Override
                    public void OnChooseDialog() {
                    }
                });
                dialogHintActivity.show(getSupportFragmentManager(), "hitDialog");
            }
        }
    }

}
