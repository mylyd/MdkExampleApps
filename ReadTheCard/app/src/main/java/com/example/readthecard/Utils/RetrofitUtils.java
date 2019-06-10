package com.example.readthecard.Utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 类说明：
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/0x/xx xx:xx
 */
public interface RetrofitUtils {
    /**
     * 名片识别
     * @param img Base64 名片流
     * @param key 用户ocrKey
     * @param secret 用户ocrSecre
     * @param typeid 识别类型 *名片识别Type 20
     * @param format 返回类型 xml/json -->null默认xml
     * @return
     */
    @FormUrlEncoded
    @POST("recogliu.do?")
    Call<GsonCardBean> postAddInfo(@Field("img") String img,@Field("key") String key,@Field("secret") String secret, @Field("typeId") String typeid, @Field("format") String format);

    /**
     * 认证合一
     * @param img1 证件流
     * @param img2 实拍流
     * @param key 用户ocrKey
     * @param secret 用户ocrSecre
     * @param typeid 识别类型 *名片识别Type 20
     * @param format 返回类型 xml/json -->null默认xml
     * @return
     */
    @FormUrlEncoded
    @POST("faceliu.do")
    Call<ResponseBody> postAddCardImage(@Field("img1") String img1,@Field("img2") String img2, @Field("key") String key, @Field("secret") String secret, @Field("typeId") String typeid, @Field("format") String format);

}
