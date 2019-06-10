package com.example.readthecard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.readthecard.Utils.GsonCardBean;
import com.example.readthecard.Utils.RetrofitUtils;
import com.example.readthecard.camera.ImageUtils;
import com.example.readthecard.content.MDK_String;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MReadTheCard";
    @BindView(R.id.photo_img)
    ImageView photoImg;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.btn_name)
    Button btnName;
    @BindView(R.id.et_job)
    EditText etJob;
    @BindView(R.id.btn_job)
    Button btnJob;
    @BindView(R.id.et_tel)
    EditText etTel;
    @BindView(R.id.btn_tel)
    Button btnTel;
    @BindView(R.id.et_company)
    EditText etCompany;
    @BindView(R.id.btn_company)
    Button btnCompany;
    @BindView(R.id.et_site)
    EditText etSite;
    @BindView(R.id.btn_site)
    Button btnSite;
    @BindView(R.id.btn_photograph)
    Button btnPhotograph;
    @BindView(R.id.btn_put)
    Button btnPut;

    private static String imgBase64;
    Bitmap imgBitMap;
    private Uri imageUri;
    List<String> mlistInfo = new ArrayList<>();
    private EditText[] etView;
    private ProgressDialog progressDialog;
    private String[] etString;
    private GsonCardBean gsonCardBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        etView = new EditText[]{etName, etJob, etTel, etCompany, etSite};
        etString = new String[]{"姓名","职位","手机","公司","地址"};
        noEndEt(null, true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("识别中，请稍等......");
    }

    private void addImgesBase64(Bitmap bit) {
        if (imgBitMap != null) {
            imgBitMap = null;
        }
        imgBitMap = bit;
        //imgBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.img_ocr);
        ByteArrayOutputStream bstream = new ByteArrayOutputStream();
        imgBitMap.compress(Bitmap.CompressFormat.PNG, 100, bstream);
        byte[] bytes = bstream.toByteArray();
        imgBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        progressDialog.show();
        postCrad();
    }


    private void noEndEt(@Nullable EditText et, boolean tvShow) {
        for (EditText endET : etView) {
            endET.setEnabled(false);
            if (tvShow) {
                if (endET.getText().toString().length() != 0) {
                    endET.setText(null);
                }
            }
        }
        if (et != null) {
            et.setEnabled(true);
        }
    }

    private void inteView() {
        etName.setText(mlistInfo.get(0));
        etJob.setText(mlistInfo.get(1));
        etTel.setText(mlistInfo.get(2));
        etCompany.setText(mlistInfo.get(3));
        etSite.setText(mlistInfo.get(4));
    }

    private void postCrad() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MDK_String.Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitUtils retrofitUtils = retrofit.create(RetrofitUtils.class);
        Call<GsonCardBean> jsoninfo = retrofitUtils.postAddInfo(imgBase64, MDK_String.key, MDK_String.secret, MDK_String.typeId, MDK_String.format);
        jsoninfo.enqueue(new Callback<GsonCardBean>() {
            @Override
            public void onResponse(Call<GsonCardBean> call, Response<GsonCardBean> response) {
                gsonCardBean = response.body();
                Log.d(TAG, "onResponse: "+gsonCardBean.getCardsinfo().get(0).getItems().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (gsonCardBean.getMessage().getStatus() == 0){
                            if (mlistInfo != null || mlistInfo.size() != 0) {
                                mlistInfo.clear();
                            }
                            for (int i = 0; i < 5; i++) {
                                mlistInfo.add(i,gsonCardBean.getCardsinfo().get(0).getItems().get(i).getContent());
                            }
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            inteView();
                        }else {
                            Toast.makeText(MainActivity.this, "读取信息失败，请重试...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<GsonCardBean> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure: 请求失败");
            }
        });
    }


    @OnClick({R.id.btn_name, R.id.btn_job, R.id.btn_tel, R.id.btn_company, R.id.btn_site, R.id.btn_photograph,R.id.btn_put})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_name://姓名
                noEndEt(etName, false);
                break;
            case R.id.btn_job://职位
                noEndEt(etJob, false);
                break;
            case R.id.btn_tel://电话
                noEndEt(etTel, false);
                break;
            case R.id.btn_company://公司
                noEndEt(etCompany, false);
                break;
            case R.id.btn_site://地址
                noEndEt(etSite, false);
                break;
            case R.id.btn_photograph://拍照
                noEndEt(null, true);
                endPhoto();
                break;
            case R.id.btn_put://上传
                //填写上传接口Post
                JcEditTextToString();
                Toast.makeText(this, "上传...-->姓名："+etName.getText().toString()+
                        "-->职位："+etJob.getText().toString()+
                        "-->电话："+etTel.getText().toString()+
                        "-->公司："+etCompany.getText().toString()+
                        "-->地址："+etSite.getText().toString()  , Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /**
     * 上传时检测 editText内容是否合理，不合理提示修改
     */
    private void JcEditTextToString(){
        for (int i = 0; i < etView.length; i++) {
            if (etView[i].length() == 0){
                Toast.makeText(this, "请填写正确的"+etString[i]+"信息", Toast.LENGTH_SHORT).show();
            }else {
                if (i == 2){
                    if (etView[i].length() != 11){
                        Toast.makeText(this, "请输入正确的"+etString[i]+"号码", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bitmap map = decodeSampledBitmapFromURL(imageUri, photoImg.getWidth(), photoImg.getHeight());
                    photoImg.setImageBitmap(ImageUtils.getRotatedBitmap(map, -90));
                    addImgesBase64(map);
                }
                break;
        }
    }

    private void endPhoto() {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intentPhoto = new Intent("android.media.action.IMAGE_CAPTURE");
        if (Build.VERSION.SDK_INT >= 24) {
            intentPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.readthecard.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentPhoto, 1);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromURL(Uri uri, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}


