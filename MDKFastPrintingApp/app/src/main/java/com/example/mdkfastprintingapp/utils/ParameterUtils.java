package com.example.mdkfastprintingapp.utils;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * 类说明：定义网络请求参数类，使用Retrofit框架编写
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/04/15 16:17
 */
public interface ParameterUtils {

    /**
     * 短信验证
     * @param iphone 电话
     * @return
     */
    @FormUrlEncoded
    @POST("sms/sendMsg?")
    Call<ResponseBody> getIphone(@Field("telphone") String iphone);

    /**
     * 身份证号查信息
     * @param num Card ID
     * @return
     */
    @FormUrlEncoded
    @POST("healthcard/getHearthCard?")
    Call<ResponseBody> postCardID(@Field("idCard") String num);

    /**
     * 上传身份证信息
     * @param addInfor 身份证信息
     * @return
     */
    @POST("idcard/insertidcard")
    Call<ResponseBody> addInfor(@Body RequestBody addInfor);

    /**
     * 提交激活码完成产品激活
     * @param activate 16位激活码
     * @return
     */
    @FormUrlEncoded
    @POST("machinecode/activeMachine?") //地址需要改变
    Call<ResponseBody> postActivate(@Field("code") String activate);


    /**
     * 获取各单位打印模板
     * @return
     */
    @GET
    Call<ResponseBody> getTemplate();
}
