package com.example.myphoto;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 类说明：
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/0x/xx xx:xx
 */
public interface HttpContent {
    @POST("android/checkface")
    Call<ResponseBody> contrastface(@Body RequestBody face);
}
