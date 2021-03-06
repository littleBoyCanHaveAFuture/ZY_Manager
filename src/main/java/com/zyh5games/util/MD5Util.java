package com.zyh5games.util;

import java.security.MessageDigest;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
public class MD5Util {
    public static final String KEY_ALGORITHM = "MD5";
    private static final String[] HEX_DIGITS = {
            "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b",
            "c", "d", "e", "f"};

    /**
     * MD5编码
     *
     * @param codingContent 要编码的内容
     * @return MD5编码之后的内容
     */
    public static String md5(String codingContent) {
        try {
            byte[] btInput = codingContent.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            String str = "";
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str += HEX_DIGITS[byte0 >>> 4 & 0xf] + HEX_DIGITS[byte0 & 0xf];
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte value : b) {
            resultSb.append(byteToHexString(value));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * MD5编码
     *
     * @param codingContent 要编码的内容
     * @param coding        编码
     * @return MD5编码之后的内容
     */
    public static String md5Encode(String codingContent, String coding) {
        String resultString = null;
        try {
            resultString = new String(codingContent);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (coding == null || "".equals(coding)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(coding)));
            }
        } catch (Exception exception) {
        }
        return resultString;
    }

    public static void main(String[] args) {
        String s = "qid=1000165?server_id=1?time=1577179162748";
        System.out.println(md5(s));
    }

}
