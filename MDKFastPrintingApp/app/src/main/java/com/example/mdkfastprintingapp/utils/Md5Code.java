package com.example.mdkfastprintingapp.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 类说明：md5算法解密
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/04/25 17:26
 */
public class Md5Code {
    private static final String HEX_NUMS_STR = "0123456789ABCDEF";
    private static final Integer SALT_LENGTH = 12;
    private static final String TAG ="MD5Code log" ;

    /**
     * 将16进制字符串转换成字节数组
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4
                    | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));
        }
        return result;
    }

    /**
     * 验证口令是否合法
     * @param password  原始密码
     * @param passwordInDb  数据库中加密的密码
     * @return true false
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static boolean validPassword(String password, String passwordInDb) {
        try {   //将16进制字符串格式口令转换成字节数组
              byte[] pwdInDb = hexStringToByte(passwordInDb);
              //声明盐变量
              byte[] salt = new byte[SALT_LENGTH];
              //将盐从数据库中保存的口令字节数组中提取出来
              System.arraycopy(pwdInDb, 0, salt, 0, SALT_LENGTH);
              //创建消息摘要对象
              MessageDigest md = null;
              md = MessageDigest.getInstance("MD5");
              //将盐数据传入消息摘要对象
              md.update(salt);
              //将口令的数据传给消息摘要对象
              md.update(password.getBytes("UTF-8"));
              //生成输入口令的消息摘要
              byte[] digest = md.digest();
              //声明一个保存数据库中口令消息摘要的变量
              byte[] digestInDb = new byte[pwdInDb.length - SALT_LENGTH];
              //取得数据库中口令的消息摘要
              System.arraycopy(pwdInDb, SALT_LENGTH, digestInDb, 0, digestInDb.length);
              //比较根据输入口令生成的消息摘要和数据库中消息摘要是否相同
              if (Arrays.equals(digest, digestInDb)) {
                  //口令正确返回口令匹配消息
                  return true;
              } else {
                  //口令不正确返回口令不匹配消息
                  return false;
              }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logs.d(TAG,"解密算法异常");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logs.d(TAG,"解密编码异常");
        }
        return false;
    }
}
