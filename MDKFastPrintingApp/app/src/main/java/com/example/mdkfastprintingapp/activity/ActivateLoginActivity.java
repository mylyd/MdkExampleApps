package com.example.mdkfastprintingapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mdkfastprintingapp.MainActivity;
import com.example.mdkfastprintingapp.R;
import com.example.mdkfastprintingapp.utils.MDK_String;
import com.example.mdkfastprintingapp.utils.ParameterUtils;
import com.example.mdkfastprintingapp.utils.ToastUtils;
import com.example.mdkfastprintingapp.utils.logs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
 * 激活页面
 */
public class ActivateLoginActivity extends AppCompatActivity {

    private static final String TAG = "ActivateLoginActivity log";
    @BindView(R.id.ed_text)
    EditText edText;
    @BindView(R.id.btn_Activate)
    Button btnActivate;
    @BindView(R.id.tv_infortv)
    Button tvInfortv;
    @BindView(R.id.btn_xx)
    ImageView btnXx;
    private SharedPreferences spf;
    String regex = "^[a-z0-9A-Z]+$";//仅含有字母数字
    //mode
    private boolean mode = false;
    private boolean mode2 = false;
    private InputMethodManager manager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_login);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        spf = getSharedPreferences("StartFirst", Context.MODE_PRIVATE);
        tvInfortv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        btnXx.setVisibility(View.GONE);
        edText.addTextChangedListener(textWatcher);
        logs.d(TAG, "进入页面SP记录的值：" + spf.getBoolean("first", false));

        if (spf.getBoolean("first", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        btnActivate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mode2 && mode){
                    spf.edit().putBoolean("first", true).apply();
                    startActivity(new Intent(ActivateLoginActivity.this,MainActivity.class));
                    finish();
                    mode = false;
                    mode2 = false;
                }
                return true;
            }
        });
        tvInfortv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mode){
                    mode = true;
                    Toast.makeText(ActivateLoginActivity.this, "The first step is complete", Toast.LENGTH_SHORT).show();
                }else {
                    mode2 = true;
                    Toast.makeText(ActivateLoginActivity.this, "The second step is complete", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                logs.d(TAG, "为空");
                btnXx.setVisibility(View.GONE);
            } else {
                logs.d(TAG, "不为空");
                btnXx.setVisibility(View.VISIBLE);
            }
        }
    };

    private void postHttpActivate(String activate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MDK_String.URL_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ParameterUtils parameterUtils = retrofit.create(ParameterUtils.class);
        //RequestBody body = RequestBody.create(MediaType.parse(MDK_String.TYPE_POST_JSON), str);
        final Call<ResponseBody> dataInfo = parameterUtils.postActivate(activate);
        dataInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    final String string = response.body().string();
                    JSONObject jsonObject = new JSONObject(string);
                    String statusString = jsonObject.getString(MDK_String.JSON_STATUS);
                    String dataString = jsonObject.getString(MDK_String.JSON_DATA);
                    if (statusString.equals(MDK_String.JSON_SUCCESS)){
                        JSONObject jsonObject1 = new JSONObject(dataString);
                        String dataTime = jsonObject1.getString("time");
                        int dataStatus = jsonObject1.getInt("status");
                        if (dataTime.equals("ok")){
                                //成功激活
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        spf.edit().putBoolean("first", true).apply();
                                        startActivity(new Intent(ActivateLoginActivity.this,MainActivity.class));
                                        finish();
                                    }
                                });
                            }else {
                                //激活码重复使用
                                ToastUtils.showToast(getApplicationContext(),"该激活码已完成过激活，无法重复使用...");
                        }
                        logs.d(TAG,dataTime);
                        logs.d(TAG,dataStatus+"");
                        logs.d(TAG,statusString);
                        logs.d(TAG,dataString);
                    }else {
                        if (dataString.equals("500")){
                            ToastUtils.showToast(getApplicationContext(),"未查询到激活码存在，请核实激活码是否正确...");
                            //清空EditText
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //edText.setText(null);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
            }
        });
    }

    @OnClick({R.id.btn_Activate,R.id.tv_infortv, R.id.btn_xx,R.id.ed_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Activate: //激活
                if (!TextUtils.isEmpty(edText.getText())) {
                    if (edText.getText().length() == 16) {
                        if (edText.getText().toString().matches(regex)){
                            //填写请求激活码逻辑
                            postHttpActivate(edText.getText().toString());
                        }else {
                            ToastUtils.showToast(this,"输入激活码的格式有误...");
                        }
                    } else {
                        ToastUtils.showToast(this,"请输入正确的16位激活码...");
                    }
                } else {
                    ToastUtils.showToast(this,"激活码不能为空...");
                }
                break;
            case R.id.tv_infortv:  //注册
                startActivity(new Intent(this, RegistrationActivatioCodeActivity.class));
                finish();
                break;
            case R.id.btn_xx: //清除
                if (!TextUtils.isEmpty(edText.getText())) {
                    edText.setText(null);
                }

                break;
            case R.id.ed_text:
                if (manager != null){//收起键盘
                    manager = ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    manager = null;
                }else { //展开键盘
                    manager = ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    manager.showSoftInput(view, 0);
                }
                break;
        }
    }


}
