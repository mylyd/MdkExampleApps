package com.example.mdkfastprintingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mdkfastprintingapp.activity.ActivateLoginActivity;
import com.example.mdkfastprintingapp.activity.CertificationActivity;
import com.example.mdkfastprintingapp.utils.MDK_String;
import com.example.mdkfastprintingapp.utils.ParameterUtils;
import com.example.mdkfastprintingapp.utils.logs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;

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
 * 类说明：主类 ，实现双功能的判断进入
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/03/20 16:54
 */
public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity log";
    @BindView(R.id.rl_HomePage_bg)
    RelativeLayout rlHomePageBg;
    @BindView(R.id.btn_InputView)
    ImageButton btnInputView;
    @BindView(R.id.btn_PrintView)
    ImageButton btnPrintView;
    @BindView(R.id.tv_TitleText)
    TextView tvTitleText;
    Bundle bundle = new Bundle();
    private SharedPreferences spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        spf = getSharedPreferences("StartFirst", Context.MODE_PRIVATE);
        if (!spf.getBoolean("first", false)){
            startActivity(new Intent(this,ActivateLoginActivity.class));
            finish();
        }
        tvTitleText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/simkai.ttf"));
        tvTitleText.getPaint().setFakeBoldText(true);
        logs.d(TAG,"this zhe shi di yi ge qi dong de MainActivity");
    }

    

    @OnClick({R.id.btn_InputView, R.id.btn_PrintView})
    public void onClick(View view) {
        if (bundle != null) {
            bundle.clear();
        }
        switch (view.getId()) {
            case R.id.btn_InputView: ;
                bundle.putInt("ActivityType",MDK_String.INFOR_TYPE);
                break;
            case R.id.btn_PrintView:
                bundle.putInt("ActivityType",MDK_String.PRINT_TYPE);
                break;
        }
        Intent intent1 = new Intent(MainActivity.this, CertificationActivity.class);
        intent1.putExtras(bundle);
        startActivity(intent1);
    }


    /**
     * 手机验证码请求验证系统
     * @param str
     */
    private void httpAddInforMation(final String str) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MDK_String.URL_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ParameterUtils parameterUtils = retrofit.create(ParameterUtils.class);
        // RequestBody body = RequestBody.create(MediaType.parse(MDK_String.TYPE_POST_JSON),str);
        Call<ResponseBody> datainfo = parameterUtils.getIphone(str);
        datainfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                logs.d(TAG, " Main ----onResponse: 请求成功");
                try {
                    String string = response.body().string();
                    JSONObject jsonObject = new JSONObject(string);
                    String TelNumber = jsonObject.getString(MDK_String.JSON_DATA);
                    logs.d(TAG,"手机编码:"+TelNumber);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                    logs.d(TAG,"String null");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                logs.d(TAG, "Main -----onFailure: 请求失败1111");
            }
        });
    }

}
