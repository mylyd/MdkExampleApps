package com.example.xyidentification.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyidentification.R;
import com.example.xyidentification.content.MDK_String;
import com.example.xyidentification.utils.CardOpData;
import com.example.xyidentification.utils.GsonCardBean;
import com.example.xyidentification.utils.ImageUtils;
import com.example.xyidentification.utils.RetrofitUtils;
import com.example.xyidentification.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardRecognitionFragment extends Fragment {
    private static final String TAG = "CardRecognitionFragment";
    @BindView(R.id.img_view)
    ImageView imgView;
    @BindView(R.id.btn_pz)
    FloatingActionButton btnBtn;
    @BindView(R.id.btnTv_name)
    TextView btnTvName;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.btnTv_job)
    TextView btnTvJob;
    @BindView(R.id.et_job)
    EditText etJob;
    @BindView(R.id.btnTv_tel)
    TextView btnTvTel;
    @BindView(R.id.et_tel)
    EditText etTel;
    @BindView(R.id.btnTv_company)
    TextView btnTvCompany;
    @BindView(R.id.et_company)
    EditText etCompany;
    @BindView(R.id.btnTv_site)
    TextView btnTvSite;
    @BindView(R.id.et_site)
    EditText etSite;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    String telRegex = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
    Unbinder unbinder;
    private Context context;
    private Uri imageUri;
    Bitmap imgBitMap;
    private String imgBase64;
    private EditText[] etView;
    private String[] etString;
    private ProgressDialog progressDialog;
    private GsonCardBean gsonCardBean;
    private List<String> mlistInfo = new ArrayList<>();
    private String[] keyJson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_recognition, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getContext();
        initView(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView(View view) {
        imgView.setBackgroundResource(R.mipmap.img_cardbg);
        etView = new EditText[]{etName, etJob, etTel, etCompany, etSite};
        keyJson = new String[]{MDK_String.POST_KEY_OCRNAME,MDK_String.POST_KEY_OCRPOSITION,MDK_String.POST_KEY_OCRTELPHONE,MDK_String.POST_KEY_OCRCOMPANY,MDK_String.POST_KEY_OCRADDRESS};
        etString = new String[]{"姓名", "职位", "手机", "公司", "地址"};

        noEndEt(null,true);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
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

    private void endPhoto() {
        File outputImage = new File(context.getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
            Intent intentPhoto = new Intent("android.media.action.IMAGE_CAPTURE");
            if (Build.VERSION.SDK_INT >= 24) {
                Log.d(TAG, "endPhoto: SDK_INT >=24");
                intentPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageUri = FileProvider.getUriForFile(context, "com.example.readthecard.fileprovider", outputImage);
            } else {
                imageUri = Uri.fromFile(outputImage);
                Log.d(TAG, "endPhoto: SDK_INT <24");
            }

            intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentPhoto, MDK_String.REQUESCODE);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException");
        } catch (SecurityException e){
            e.printStackTrace();
            Log.d(TAG, "SecurityException");
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MDK_String.REQUESCODE:
                if (resultCode == RESULT_OK) {
                    Bitmap map = decodeSampledBitmapFromURL(imageUri, imgView.getWidth(), imgView.getHeight());
                    imgView.setImageBitmap(ImageUtils.getRotatedBitmap(map, -90));
                    addImgesBase64(map);
                }
                break;
        }
    }

    private void postCrad() {
        progressDialog.setMessage("识别中，请稍等......");
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
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (gsonCardBean.getMessage().getStatus() == 0){
                                if (mlistInfo != null || mlistInfo.size() != 0) {
                                    mlistInfo.clear();
                                }
                                mlistInfo = CardOpData.onDataCardInfo(gsonCardBean);
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                showData();
                            }else {
                                ToastUtils.showToast(context,"读取信息失败，请重试...");
                            }
                        }
                    });
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GsonCardBean> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure: 请求失败");
            }
        });
    }

    private void showData() {
        etName.setText(mlistInfo.get(0));
        etJob.setText(mlistInfo.get(1));
        etTel.setText(mlistInfo.get(2));
        etCompany.setText(mlistInfo.get(3));
        etSite.setText(mlistInfo.get(4));
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
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @OnClick({R.id.btn_pz,R.id.btnTv_name, R.id.btnTv_job, R.id.btnTv_tel, R.id.btnTv_company, R.id.btnTv_site, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pz://拍照
                endPhoto();
                break;
            case R.id.btnTv_name://修改-name
                noEndEt(etName,false);
                break;
            case R.id.btnTv_job:
                noEndEt(etJob,false);
                break;
            case R.id.btnTv_tel:
                noEndEt(etTel,false);
                break;
            case R.id.btnTv_company:
                noEndEt(etCompany,false);
                break;
            case R.id.btnTv_site:
                noEndEt(etSite,false);
                break;
            case R.id.btn_submit://提交
                /*ToastUtils.showToast(context,"姓名："+etName.getText().toString()+"\n" +
                        "-->职位："+etJob.getText().toString()+"\n" +
                        "-->电话："+etTel.getText().toString()+"\n" +
                        "-->公司："+etCompany.getText().toString()+"\n" +
                        "-->地址："+etSite.getText().toString());*/
                JcEditTextToString();

                break;
        }
    }

    private void postAddCard(String cardInfo){
        progressDialog.setMessage("信息正在上传，请稍等...");
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MDK_String.Url_Card)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitUtils retrofitUtils = retrofit.create(RetrofitUtils.class);
        RequestBody body = RequestBody.create(MediaType.parse(MDK_String.TYPE_POST_JSON),cardInfo);
        Call<ResponseBody> jsoncard = retrofitUtils.postAddCard(body);
        jsoncard.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String string = response.body().string();
                    Log.d(TAG, "onResponse: "+string);
                    JSONObject jsonObject = new JSONObject(string);
                    onToast(jsonObject.getString(MDK_String.JSON_STATUS));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure: 请求失败");
            }
        });
    }

    private void JcEditTextToString() {
        if (etView[0].length() != 0){
            if (etView[1].length() != 0){
                if (etView[2].length() != 0 && etView.length != 11){
                    if (etView[2].getText().toString().matches(telRegex)){
                        if (etView[3].length() != 0){
                            if (etView[4].length() != 0){
                               postAddCard(postAddCardJson());
                            }else {
                                ToastUtils.showToast(context, "请填写正确的"+etString[4]+"信息");
                            }
                        }else {
                            ToastUtils.showToast(context, "请填写正确的"+etString[3]+"信息");
                        }
                    }else {
                                     ToastUtils.showToast(context, "请填写正确"+etString[2]+"号的格式");
                    }
                }else {
                    ToastUtils.showToast(context, "请填写正确的11位"+etString[2]+"号信息");
                }
            }else {
                ToastUtils.showToast(context, "请填写正确的"+etString[1]+"信息");
            }
        }else {
            ToastUtils.showToast(context, "请填写正确的"+etString[0]+"信息");
        }

    }

    private String postAddCardJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 0; i <keyJson.length; i++) {
                jsonObject.put(keyJson[i],etView[i].getText().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void onToast(final String str){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (str.equals("200")){
                    Snackbar.make(getView(),"名片信息上传成功",Snackbar.LENGTH_LONG)
                            .setAction("继续", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    endPhoto();
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.actionbarTextColor))
                            .show();
                    //ToastUtils.showToast(context,"名片信息上传成功");
                }else {
                    ToastUtils.showToast(context,"名片信息上传失败");
                }
            }
        });
    }
}
