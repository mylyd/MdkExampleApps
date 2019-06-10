package com.example.mdkfastprintingapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdkfastprintingapp.R;
import com.example.mdkfastprintingapp.adapter.RecyclerViewAdapter;
import com.example.mdkfastprintingapp.bean.RecylerViewGetData;
import com.example.mdkfastprintingapp.dialog.DialogHintActivity;
import com.example.mdkfastprintingapp.utils.MDK_String;
import com.example.mdkfastprintingapp.utils.ParameterUtils;
import com.example.mdkfastprintingapp.utils.PkCode;
import com.example.mdkfastprintingapp.utils.logs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * 类说明：打印页主类 ，@实现模板选择，@实现打印功能
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/03/20 16:54
 */
public class PrintHomeDomeActivity extends AppCompatActivity {
    private String TAG = "PrintHomeDomeActivity log";
    Context context = this;

    @BindView(R.id.btn_backHome)
    ImageView btnBackHome;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.img_code_Z)
    ImageView imgCodeZ;
    @BindView(R.id.img_code_F)
    ImageView imgCodeF;
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
    RelativeLayout view_rl;

    List<RecylerViewGetData> mlist = new ArrayList<>();//初始化模板数据
    List<String> listData = new ArrayList<>();
    Bitmap imgPhotoBitMap = null;
    Bitmap imgShowBitMap = null;
    boolean first = true;
    int index = 0;
    private ProgressDialog progressDialog;

    View[] viewsShow = new View[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printhomedemo);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        //progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在加载...");


        setRecyclerViewData();
        setIDInfoData();
        //setData(MDK_String.CODE_BAR,"1234567890","合格");
        setEnshow();
    }

    private void setEnshow(){
        viewsShow[0] = btnBackHome;
        viewsShow[1] = view_rl;
        viewsShow[2] = btnPrint;
        viewsShow[3] = recyclerView;
        for (int i = 0; i < viewsShow.length; i++) {
            if (viewsShow[i].getVisibility() == View.GONE){
                viewsShow[i].setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置字体为宋体
     * */
    void setFontStyle(){
        /*TextView[] tvStringType = {tvName,tvAge,tvSex,tvExamination,tvTime,tvNumberID,tvSite};
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/simsun.ttf");
        for (TextView textView : tvStringType) {
            textView.setTypeface(tf);
           //textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.getPaint().setFakeBoldText(true);
        }*/
    }

    /**
     * 获取读取所得身份证的各项信息，存入String[];
     */
    void setIDInfoData() {
        /*数据的取出*/
        Bundle bundle = this.getIntent().getExtras();
        listData.add(0,bundle.getString("birth"));
        listData.add(1,bundle.getString("IDNo"));
        listData.add(2,bundle.getString("name"));
        listData.add(3,bundle.getString("sex"));
        imgPhotoBitMap = bundle.getParcelable("photo");

        if (imgPhotoBitMap == null){
            logs.d(TAG, "setIDInfoData: Birmap == null");
        }
        
        HttpCardInforMation(listData.get(1));
    }

    /**
     * 加载模板用RecyclerView展示
     */
    void setRecyclerViewData() {
        //填充模板data
        int[] ints = new int[]{R.drawable.img_bar, R.drawable.img_qr, R.drawable.img_white, R.drawable.img_add};
        for (int i = 0; i < ints.length; i++) {
            mlist.add(new RecylerViewGetData(ints[i]));
        }
        //加载视图
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mlist);
        recyclerView.setAdapter(adapter);
        //回掉item点击事件，逻辑写为切模板类型
        adapter.getSwitchTheTemplate(new RecyclerViewAdapter.SwitchTheTemplateOclick() {
            @Override
            public void onShowWord(int position) {
                Bitmap bt = null;
                if (position == 0){
                    setData(MDK_String.CODE_BAR,listData.get(5),listData.get(4));
                }else if (position == 1){
                    setData(MDK_String.CODE_QR,listData.get(5),listData.get(4));
                }else if (position == 2){
                    Toast.makeText(PrintHomeDomeActivity.this, "模板三...", Toast.LENGTH_SHORT).show();
                }else if (position == 3){
                    Toast.makeText(PrintHomeDomeActivity.this, "暂未增加添加模板功能...", Toast.LENGTH_SHORT).show();
                }
               // timer.schedule(task1,100);
                if (index == 0){
                    index ++;
                }else {
                    imgCodeZ.setImageBitmap(showTopo(view_rl, bt));
                    index = 0;
                }
            }
        });
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //setFontStyle();
                    imgCodeZ.setImageBitmap(showTopo(view_rl, imgShowBitMap));
                    imgCodeF.setImageDrawable(getResources().getDrawable(R.mipmap.img_code_rear));
                    timer.cancel();
                }
            });
        }
    };

    Timer timer = new Timer();

    /**
     * 填充样板板数据
     * @param infoType 生成code类型
     * @param numberId 健康证证号
     * @param examination 体检状态
     * */
    void setData(int infoType,String numberId,String examination){
        String info = "";
        if (infoType == MDK_String.CODE_BAR){
            info = numberId;
            setStyleBar(info);
        }else if (infoType == MDK_String.CODE_QR){
            info = numberId+";"+listData.get(2).substring(0,10)+";\n"+
                    setString_int(listData.get(1),0,6)+"********"+setString_int(listData.get(1),14,18)+";\n"+ listData.get(6);
            setStyleQR(info,numberId);
        }

        tvTitle.setText("健      康      证");
        tvSite.setText("武汉玛迪卡智能科技有限公司");
        imgPhoto.setImageBitmap(imgPhotoBitMap);//填充头像
        //填充姓名，性别，年龄，体检信息，有效期等等...
        tvName.setText("姓名:  "+listData.get(2));
        tvSex.setText("性别:  "+listData.get(3));
        imgChapter.setImageResource(R.drawable.bg_y);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        Date date = new Date(System.currentTimeMillis());
        String strTime = simpleDateFormat.format(date);
        int yes1 = setString_int(strTime,0,4); //单签
        int yes2 = setString_int(listData.get(0),0,4);
        int mon1 = setString_int(strTime,4,6);
        int mon2 = setString_int(listData.get(0),4,6);
        tvAge.setText("年龄:  "+CalculateTheAge(yes1,yes2,mon1,mon2));
        tvTime.setText("有效期:  "+listData.get(6));
        tvExamination.setText("体检:  "+examination);

        if (first){
            timer.schedule(task,100);
            first = false;
        }
    }

    /**
     * 通过出生日期计算年龄大小(向下取整)
     * @param y1 当前年份
     * @param y2 出生年份
     * @param m1 当前月份
     * @param m2 出生月份
     * @return 返回int 即年龄大小
     */
    int CalculateTheAge(int y1,int y2,int m1,int m2){
        logs.d("年龄大小······", "CalculateTheAge: 当前 - 月份   :"+m1+"  "+m2+"  "+(m1 - m2));
        if (m1 - m2 < 0){
            logs.d("年龄大小······", "CalculateTheAge: 当前 - 月份 < 0  :"+((y1-y2)-1));
            return (y1-y2)-1;
        }else {
            logs.d("年龄大小······", "CalculateTheAge: 当前 - 月份 >= 0  :"+(y1-y2));
            return y1 - y2;
        }
    }


    /**
     * 模板样式位移 @二维码位图
     * @param infoStr 二维码存入信息
     * @param num 健康证证号
     */
    void setStyleQR(String infoStr,String num){
        RelativeLayout.LayoutParams lp_tv = (RelativeLayout.LayoutParams) tvNumberID.getLayoutParams();
        lp_tv.leftMargin = 140;
        lp_tv.topMargin = 145;
        tvNumberID.setLayoutParams(lp_tv);
        tvNumberID.setText("证号:  "+num);

        RelativeLayout.LayoutParams lp_img = (RelativeLayout.LayoutParams) imgYard.getLayoutParams();
        lp_img.width = 65 ;
        lp_img.height = 65 ;
        lp_img.leftMargin = 290;
        imgYard.setLayoutParams(lp_img);
        imgYard.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgYard.setImageBitmap(PkCode.generateCode(MDK_String.CODE_QR,imgYard,infoStr));

        view_rl.invalidate();
    }

    /**
     * 模板样式位移 @条形码位图
     * @param num 健康证证号
     */
    void setStyleBar(String num){
        RelativeLayout.LayoutParams lp_tv = (RelativeLayout.LayoutParams) tvNumberID.getLayoutParams();
        lp_tv.leftMargin = 185;
        lp_tv.topMargin = 185;
        tvNumberID.setLayoutParams(lp_tv);
        tvNumberID.setText(num);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imgYard.getLayoutParams();
        lp.width = 170 ;
        lp.height = 40 ;
        lp.leftMargin = 175;
        imgYard.setLayoutParams(lp);
        imgYard.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imgYard.setImageBitmap(PkCode.generateCode(MDK_String.CODE_BAR,imgYard,num));

        view_rl.invalidate();
    }


    /**
     * 截图功能
     * @param views 需要截取的View
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
        bitmap = Bitmap.createBitmap(bitmap,viewLocationArray[0],viewLocationArray[1],outWidth,outHeight);
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
     * 请求服务器获取数据
     * @param sInfo cardID
     */
    private void HttpCardInforMation(final String sInfo){
        progressDialog.show();

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
                    String string = response.body().string();
                    if (!TextUtils.isEmpty(string)){
                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            if (jsonObject.getString(MDK_String.JSON_DATA) != null){
                                JSONObject object = new JSONObject(jsonObject.getString(MDK_String.JSON_DATA));
                                listData.add(4,object.getString(MDK_String.JSON_MEDICAL));//体检状态
                                listData.add(5,object.getString(MDK_String.JSON_HEALTHMUM));//健康证编号
                                listData.add(6,object.getString(MDK_String.JSON_HEALTHTIME));//到期日期
                                listData.add(7,object.getString(MDK_String.JSON_GZ));
                                listData.add(8,object.getString(MDK_String.JSON_ADDRESS));//地址
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listData.get(4).equals("合格")){
                                            //更新数据ui
                                            setData(MDK_String.CODE_BAR,listData.get(5),listData.get(4));
                                        }else {
                                            for (int i = 0; i < viewsShow.length; i++) {
                                                viewsShow[i].setVisibility(View.GONE);
                                            }
                                            DialogHintActivity dialogHintActivity = new DialogHintActivity("体检状态不合格无法打印");
                                            dialogHintActivity.setOnDialogHintText(new DialogHintActivity.OnDialogHintText() {
                                                @Override
                                                public void OnChooseDialog() {
                                                    finish();
                                                }
                                            });
                                            dialogHintActivity.show(getSupportFragmentManager(),"this print information");
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            }else {
                                logs.d(TAG, "onResponse: JSON_STATUS  == null ");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            logs.d(TAG, "onResponse: 111111异常");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //更新数据ui
                                    for (int i = 0; i < viewsShow.length; i++) {
                                        viewsShow[i].setVisibility(View.GONE);
                                    }
                                    DialogHintActivity dialogHintActivity = new DialogHintActivity("暂时无法查询此人信息...");
                                    dialogHintActivity.setOnDialogHintText(new DialogHintActivity.OnDialogHintText() {
                                        @Override
                                        public void OnChooseDialog() {
                                            finish();
                                        }
                                    });
                                    dialogHintActivity.show(getSupportFragmentManager(),"this print information");
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }else {
                        logs.d(TAG, "onResponse: log--string == null ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.fillInStackTrace();
                logs.d(TAG, "onResponse: 请求失败");
            }
        });
    }

    @OnClick({R.id.btn_backHome, R.id.btn_print})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_backHome:
                finish();
                break;
            case R.id.btn_print:
                Toast.makeText(this, "此接口正在开发中.....", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }else {
            timer.cancel();
        }
    }

}
