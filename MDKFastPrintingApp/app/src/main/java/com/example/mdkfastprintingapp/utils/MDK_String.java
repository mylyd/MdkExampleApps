package com.example.mdkfastprintingapp.utils;

import com.example.mdkfastprintingapp.R;

/**
 * 类说明：静态常数
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/03/24 16:30
 */
public class MDK_String {
    public MDK_String(){
    }

    public static final String ip = "39.98.227.70";  /**服务器 ip*/
    public static final String port_8089 = "8089";   /**服务器 端口1 8089*/
    public static final String port_8082 = "8082";   /**服务器 端口2 8082*/

    /**请求Url_8082 (半截 @全程须包含ParameterUilts.class内的)*/
    public static final String URL_VERSION = "http://"+ip+":8082/mdk2019/";

    /**请求Url_8089 (半截 @全程须包含ParameterUilts.class内的)*/
    public static final String URL_VERSION_8089= "http://"+ip+":8089/mdk2019/";

    /**请求头Type*/
    public static final String TYPE_POST_TEXT_XML = "text/xml; charset=utf-8";
    public static final String TYPE_POST_TEXT_HTML = "application/x-www-form-urlencoded";
    public static final String TYPE_POST_JSON = "application/json; charset=utf-8";

    /**Activity页面Type*/
    public static final int INFOR_TYPE = 935; //信息录入
    public static final int PRINT_TYPE = 936; //证卡打印

    public static final int USB_TYPE = 10001; //USB接口
    public static final int BLUETOOTH_TYPE = 10002; //蓝牙接口
    public static final int WIRELESS_TYPE = 10003;  //无线网络接口
    public static final int CODE_QR = 10123; //二维码模式
    public static final int CODE_BAR = 10124; //条形码模式

    /**服务器返回JSON解析字段名（不完善）*/
    public static final String JSON_STATUS = "status";
    public static final String JSON_DATA = "data";
    public static final String JSON_NAME = "name";
    public static final String JSON_AGE = "age";
    public static final String JSON_MEDICAL = "medical";
    public static final String JSON_HEALTHMUM = "healthNum";
    public static final String JSON_HEALTHTIME = "healthTime";
    public static final String JSON_QRCARD = "qrCode";
    public static final String JSON_GZ = "gz";
    public static final String JSON_GENDER = "gender";
    public static final String JSON_IDCARD = "idCard";
    public static final String JSON_ADDRESS = "address";
    public static final String JSON_SUCCESS = "success";
    public static final String JSON_FAIL = "fail";

    public static final int MUSIC_1 = R.raw.voice_prompt_1; //请刷身份证
    public static final int MUSIC_2 = R.raw.voice_prompt_2; //请抬头直视摄像头
    public static final int MUSIC_3 = R.raw.voice_prompt_3; //正在比对，请稍等
    public static final int MUSIC_4 = R.raw.voice_prompt_4; //人脸比对校验失败
    public static final int MUSIC_5 = R.raw.voice_prompt_5; //人脸比对校验成功
    public static final int MUSIC_6 = R.raw.voice_prompt_6; //证件上传成功
    public static final int MUSIC_7 = R.raw.voice_prompt_7; //证件上传失败
    public static final int MUSIC_8 = R.raw.voice_prompt_8; //录入未满一年时效
    public static final int MUSIC_9 = R.raw.voice_prompt_9; //体检信息未合格，无法打印
    public static final int MUSIC_10 = R.raw.voice_prompt_10; //未查询到您的相关信息，请重试
    public static final int MUSIC_11 = R.raw.voice_prompt_11; //未知错误

}
