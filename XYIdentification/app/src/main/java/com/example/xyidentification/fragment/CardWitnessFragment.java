package com.example.xyidentification.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyidentification.R;
import com.example.xyidentification.content.MDK_String;
import com.example.xyidentification.utils.GsonCardBean;
import com.example.xyidentification.utils.ImageUtils;
import com.example.xyidentification.utils.RetrofitUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
public class CardWitnessFragment extends Fragment {
    private static final String TAG = "CardWitnessFragment";
    Bitmap bit1;//证件图
    Bitmap bit2;//原图
    String img1;
    String img2;
    @BindView(R.id.img1)
    ImageView imageView1;
    @BindView(R.id.img2)
    ImageView imageView2;
    @BindView(R.id.btn_paizhao)
    Button btnPaizhao;
    @BindView(R.id.btn_contrast)
    Button btnContrast;
    @BindView(R.id.tv_contrast)
    TextView tvContrast;
    Unbinder unbinder;

    Context context = getActivity();
    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_witness, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private String toImageBase(Bitmap bit) {
        String base64 = "";
        if (base64.length() != 0) {
            base64 = null;
        }
        //imgBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.img_ocr);
        ByteArrayOutputStream bstream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG, 100, bstream);
        byte[] bytes = bstream.toByteArray();
        base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return base64;
    }


    private void toYuanjian(){
        bit1 = BitmapFactory.decodeResource(getResources(), R.drawable.cy);
        imageView2.setImageBitmap(bit1);
        img1 = toImageBase(bit1);
        img2 = toImageBase(bit2);
        postAddCard();
    }

    private void postAddCard(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MDK_String.Url_Card)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitUtils retrofitUtils = retrofit.create(RetrofitUtils.class);

        Call<ResponseBody> jsoninfo = retrofitUtils.postAddCardImage(img1,img2, MDK_String.key, MDK_String.secret, "9999", MDK_String.format);
        jsoninfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String string = response.body().string();
                    Log.d(TAG, "onResponse: "+string);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
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

    private void endPhoto() {
        File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MDK_String.REQUESCODE:
                if (resultCode == RESULT_OK) {
                    Bitmap map = decodeSampledBitmapFromURL(imageUri, imageView1.getWidth(), imageView1.getHeight());
                    imageView1.setImageBitmap(map);
                    bit2 = map;
                }
                break;
        }
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
            BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri), null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_paizhao, R.id.btn_contrast})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_paizhao:
                endPhoto();
                break;
            case R.id.btn_contrast:
                toYuanjian();
                break;
        }
    }
}
