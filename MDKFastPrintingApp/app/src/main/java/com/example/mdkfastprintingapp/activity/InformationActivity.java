package com.example.mdkfastprintingapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mdkfastprintingapp.MainActivity;
import com.example.mdkfastprintingapp.R;
import com.example.mdkfastprintingapp.bean.InforData;
import com.example.mdkfastprintingapp.dialog.DialogHintActivity;
import com.example.mdkfastprintingapp.utils.MDK_String;
import com.example.mdkfastprintingapp.utils.ParameterUtils;
import com.example.mdkfastprintingapp.utils.StringToBitmapUtils;
import com.example.mdkfastprintingapp.utils.ToastUtils;
import com.example.mdkfastprintingapp.utils.logs;
import com.example.mdkfastprintingapp.voice.VoicePromptUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 类说明：Information录入页面
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/04/15 16:54
 */
public class InformationActivity extends AppCompatActivity {
    private static final String TAG = "InformationActivity log";
    List<String> mlist = new ArrayList<>();
    @BindView(R.id.btn_back1)
    ImageView btnBack1;
    @BindView(R.id.img_inforface)
    ImageView imgFace;

    @BindView(R.id.img_infor_photo)
    ImageView imgInforPhoto;
    @BindView(R.id.img_infor_bar)
    ImageView imgInforBar;
    @BindView(R.id.tv_infor_Site)
    TextView tvInforSite;
    @BindView(R.id.tv_infor_Carid)
    TextView tvInforCarid;
    @BindView(R.id.tv_infor_name)
    TextView tvInforName;
    @BindView(R.id.tv_infor_age)
    TextView tvInforAge;
    @BindView(R.id.tv_infor_sex)
    TextView tvInforSex;
    @BindView(R.id.tv_infor_state)
    TextView tvInforState;
    @BindView(R.id.tv_infor_time)
    TextView tvInforTime;
    private DialogHintActivity dialogHintActivity;
    private Bitmap bitmapPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        ButterKnife.bind(this);
        getAddUpData();
    }

    /**
     * 填充数据
     */
    private void getAddUpData() {
        InforData inforData = (InforData) getIntent().getSerializableExtra("information");

        String birth = inforData.getBirth();
        mlist.add(inforData.getName());
        mlist.add(inforData.getSex());
        mlist.add(String.valueOf(CalculateTheAge(setString_int(birth, 0, 4), setString_int(birth, 4, 6))));
        mlist.add(inforData.getNation());
        mlist.add(inforData.getAddress());
        mlist.add(inforData.getIDNo());
        mlist.add(inforData.getEffectDate());
        mlist.add(inforData.getExpireDate());
        mlist.add(inforData.getDepartment());
        bitmapPhoto = StringToBitmapUtils.stringToBitmap(getIntent().getExtras().getString("photo"));
        logs.d(TAG, "getAddData: " + inforData.getUri());
        for (int i = 0; i < mlist.size(); i++) {
            logs.d(TAG, "getAddData: " + mlist.get(i));
        }
        showDataInfor();
    }

    /**
     * 通过出生日期计算年龄大小(向下取整)
     * y1 当前年份
     * y2 出生年份
     * m1 当前月份
     * m2 出生月份
     * @return 返回int 即年龄大小
     */
    int CalculateTheAge(int y2, int m2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        Date date = new Date(System.currentTimeMillis());
        String strTime = simpleDateFormat.format(date);
        int y1 = setString_int(strTime, 0, 4);
        int m1 = setString_int(strTime, 4, 6);

        if (m1 - m2 < 0) {
            logs.d("年龄大小······", "CalculateTheAge: 当前 - 月份 < 0  :" + ((y1 - y2) - 1));
            return (y1 - y2) - 1;
        } else {
            logs.d("年龄大小······", "CalculateTheAge: 当前 - 月份 >= 0  :" + (y1 - y2));
            return y1 - y2;
        }
    }

    /**
     * 截取字符串
     *
     * @param arg  需要截取的字符串
     * @param ins1 开始位
     * @param ins2 结束位
     * @return
     */
    private int setString_int(String arg, int ins1, int ins2) {
        if (arg.isEmpty()){
            new TimeoutException("arg is null");
            return 0;
        }
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
     * 身份证信息打包成JSON数据
     */
    private void addData() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("name",mlist.get(0).substring(0, 4));
            jsonObject.put("sex", mlist.get(1));
            jsonObject.put("age", mlist.get(2));
            jsonObject.put("nation", mlist.get(3));
            jsonObject.put("address", mlist.get(4));
            jsonObject.put("idcardnum", mlist.get(5));
            jsonObject.put("starttime", mlist.get(6));
            jsonObject.put("endtime", mlist.get(7));
            jsonObject.put("psb", mlist.get(8));
            jsonObject.put("photo", "zzzzzzzzz");
            jsonObject.put("photo", StringToBitmapUtils.bitmapToString(bitmapPhoto));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialogHintActivity = new DialogHintActivity("正在上传，请稍等....");
        dialogHintActivity.setOnDialogHintText(new DialogHintActivity.OnDialogHintText() {
            @Override
            public void OnChooseDialog() {
                finish();
            }
        });
        dialogHintActivity.show(getSupportFragmentManager(),"Information");
        logs.d(TAG,jsonObject.toString());
         httpAddInforMation(jsonObject.toString());
    }

    /**
     * 填写页面数据
     * */
    private void showDataInfor(){
        tvInforName.setText(getString(R.string.card_name)+mlist.get(0));
        tvInforSex.setText(getString(R.string.card_gender)+mlist.get(1));
        tvInforAge.setText(getString(R.string.card_age)+mlist.get(2));
        tvInforState.setText(getString(R.string.card_medical)+"未审核");
        tvInforCarid.setText("证号：xxxxxxxxxxx");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");
        Date date1 = new Date(System.currentTimeMillis());
        String strTime1 = simpleDateFormat1.format(date1);
        tvInforTime.setText(getString(R.string.card_health_time)+(setString_int(strTime1,0,4)+1)+" - "+setString_int(strTime1,5,6)+" - "+(setString_int(strTime1,7,8)+7));
        imgInforPhoto.setImageBitmap(bitmapPhoto);
        addData();
    }

    /**
     * 上传证件信息
     * @param str 上传的JSON数据
     */
    private void httpAddInforMation(final String str) {
        String struri = "http://192.168.1.111:8086/idcard/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MDK_String.URL_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ParameterUtils parameterUtils = retrofit.create(ParameterUtils.class);
        RequestBody body = RequestBody.create(MediaType.parse(MDK_String.TYPE_POST_JSON), str);
        Call<ResponseBody> datainfo = parameterUtils.addInfor(body);
        datainfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    final String string = response.body().string();
                    logs.d(TAG, "onResponse: 请求结果：：：" + string);
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.getString(MDK_String.JSON_DATA) != null){
                        final String dataString = jsonObject.getString(MDK_String.JSON_DATA);
                        final int raw ;
                        if (dataString.equals("上传成功")){
                            raw = MDK_String.MUSIC_6;
                        }else if (dataString.equals("上传失败")){
                            raw = MDK_String.MUSIC_7;
                        }else if (dataString.equals("有效期未满11个月")){
                            raw = MDK_String.MUSIC_8;
                        }else {
                            raw = MDK_String.MUSIC_11;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogHintActivity.dismiss();
                                ToastUtils.showToast(InformationActivity.this,dataString);
                                VoicePromptUtils.onPlay(InformationActivity.this,raw);
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    logs.d(TAG, " Main ----onResponse: 请求异常");
                } catch (NullPointerException e){
                    e.printStackTrace();
                    logs.d(TAG,"response.body().string() = null ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                logs.d(TAG, "Main -----onFailure: 请求失败---- 上传失败");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(InformationActivity.this,"请检查网络状态或则服务器连接状态...");
                        finish();
                    }
                });
            }
        });
    }

    @OnClick({R.id.btn_back1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back1:
                finish();
                break;

        }
    }
}
