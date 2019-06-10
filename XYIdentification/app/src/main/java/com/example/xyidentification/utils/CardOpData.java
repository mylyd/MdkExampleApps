package com.example.xyidentification.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.xyidentification.content.MDK_String;

import java.util.ArrayList;
import java.util.List;

/**
 * 类说明：
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/0x/xx xx:xx
 */
public class CardOpData {
    /**
     * list.get(0) == name;
     * list.get(1) == job;
     * list.get(2) == tel;
     * list.get(3) == company;
     * list.get(4) == site;
     */
    private static List list = new ArrayList();
    private static List<GsonCardBean.CardsinfoBean.ItemsBean> list_items;
    private static String cName;
    private static String cJob;
    private static String cTel;
    private static String cCompany;
    private static String cSite;
    private static final int CompanyStringLength = 10;

    /**
     * @param gsonCardBean Gson 对象;
     * @return 携带正确数据的 list
     */
    public static List onDataCardInfo(GsonCardBean gsonCardBean){
        if (list.size() != 0 ){
            list.clear();
            cName = cJob = cTel = cCompany = cSite = null;
        }
        int itemSize = gsonCardBean.getCardsinfo().get(0).getItems().size();
        list_items = gsonCardBean.getCardsinfo().get(0).getItems();
        for (int i = 0; i < itemSize-1; i++) { //次数size-1, 防止i+1越域
            if (list_items.get(i).getNID().equals(list_items.get((i+1)).getNID())){
                //处理相同的数据
                toSame(list_items.get(i).getDesc(),list_items.get(i).getContent(),list_items.get((i+1)).getDesc(),list_items.get((i+1)).getContent());
            }else {
                //前后两条不重复时执行
                toClassify(list_items.get(i).getDesc(),list_items.get(i).getContent());
            }
        }
        return outData();
    }

    private static List outData(){
        if (!TextUtils.isEmpty(cName)){
            list.add(0,cName);
        }else {
            list.add(0,"");
        }
        if (!TextUtils.isEmpty(cJob)){
            list.add(1,cJob);
        }else {
            list.add(1,"");
        }
        if (!TextUtils.isEmpty(cTel)){
            list.add(2,cTel);
        }else {
            list.add(2,"");
        }
        if (!TextUtils.isEmpty(cCompany)){
            list.add(3,cCompany);
        }else {
            list.add(3,"");
        }
        if (!TextUtils.isEmpty(cSite)){
            list.add(4,cSite);
        }else {
            list.add(4,"");
        }
        return list;
    }

    /**
     * 处理临近相同数据组
     * @param desc1 desc (i)
     * @param content1 content (i)
     * @param desc2 desc (i+1)
     * @param content2 content (i+1)
     */
    private static void toSame(String desc1 , String content1 ,String desc2 ,String content2){
        StringBuffer stringBuffer = new StringBuffer();
        if (desc1.equals(desc2)) {
            if (content1.length() >= CompanyStringLength) {
                if (content1.substring((content1.length() - 2)).equals(MDK_String.Op_Data_GongSi)) {
                    cCompany = content1;
                }
            } else if (content2.length() >= CompanyStringLength) {
                if (content2.substring((content2.length() - 2)).equals(MDK_String.Op_Data_GongSi)) {
                    cCompany = content2;
                }
            } else {
                if (desc1.equals(MDK_String.Op_Data_ZhiWu) && desc2.equals(MDK_String.Op_Data_ZhiWu)){
                    if (content2.length() <= 5){
                        stringBuffer.append(content1).append("/").append(content2);
                    }else {
                        stringBuffer.append(content1);
                    }
                    cJob = stringBuffer.toString();
                }
            }
        }
    }


    /**
     * 分类赋值
     * @param desc desc
     * @param content content
     */
    private static void toClassify(String desc , String content){
        ifCompany(content);
        switch (desc){
            case MDK_String.Op_Data_XingMing:
                cName = content;
                break;
            case MDK_String.Op_Data_ZhiWu:
                if (TextUtils.isEmpty(cJob)){ //多次重复职位信息取第一个为准
                    cJob = content;
                }
                break;
            case MDK_String.Op_Data_ShouJi:
                cTel = content;
                break;
            case MDK_String.Op_Data_GongSi:
                cCompany = content;
                break;
            case MDK_String.Op_Data_DiZhi:
                cSite = content;
                break;
        }
    }

    /**
     * 判断是否是公司名称错位并校正
     * @param str content
     */
    private static void ifCompany(String str){
        if (str.length() >= CompanyStringLength){
            if (str.substring((str.length() - 2)).equals(MDK_String.Op_Data_GongSi)){
                cCompany = str;
                return;
            }
        }
    }


}
