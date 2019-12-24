package com.ssm.promotion.core.util;

import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ant on 2015/4/11.
 */
public class RSAUtils {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA = "SHA1WithRSA";
    private static final Logger log = Logger.getLogger(RSAUtils.class);
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 生成公钥和私钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> generateKeys() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 使用公钥对RSA签名有效性进行检查
     *
     * @param content       待签名数据
     * @param sign          签名值
     * @param publicKey     爱贝公钥
     * @param input_charset 编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String publicKey, String input_charset) {
        return verify(content, sign, publicKey, input_charset, SIGNATURE_ALGORITHM_MD5);
    }

    /**
     * 使用公钥对RSA签名有效性进行检查
     *
     * @param content       待签名数据
     * @param sign          签名值
     * @param publicKey     爱贝公钥
     * @param input_charset 编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String publicKey, String input_charset, String algorithm) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            byte[] encodedKey = Base64.decode2Bytes(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


            Signature signature = Signature
                    .getInstance(algorithm);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(input_charset));

            return signature.verify(Base64.decode2Bytes(sign));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 使用私钥对数据进行RSA签名
     *
     * @param content       待签名数据
     * @param privateKey    商户私钥
     * @param input_charset 编码格式
     * @return 签名值
     */
    public static String sign(String content, String privateKey, String input_charset) {
        return sign(content, privateKey, input_charset, SIGNATURE_ALGORITHM_MD5);
    }

    /**
     * 使用私钥对数据进行RSA签名
     *
     * @param content       待签名数据
     * @param privateKey    商户私钥
     * @param input_charset 编码格式
     * @return 签名值
     */
    public static String sign(String content, String privateKey, String input_charset, String algorithm) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode2Bytes(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            Signature signature = Signature
                    .getInstance(algorithm);

            signature.initSign(priKey);
            signature.update(content.getBytes(input_charset));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获得公钥
     */
    public static RSAPublicKey getpublicKey(Map<String, Object> keyMap) {
        RSAPublicKey publicKey = (RSAPublicKey) keyMap.get(PUBLIC_KEY);
        return publicKey;
    }

    /**
     * 获得私钥
     */
    public static RSAPrivateKey getPrivateKey(Map<String, Object> keyMap) {
        RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get(PRIVATE_KEY);
        return privateKey;
    }

    /**
     * 公钥加密
     */
    public static byte[] encrypt(byte[] data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherBytes = cipher.doFinal(data);
        return cipherBytes;
    }

    /**
     * 私钥解密
     */
    public static byte[] decrypt(byte[] data, RSAPrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plainBytes = cipher.doFinal(data);
        return plainBytes;
    }

    public static void main(String[] args) throws Exception {

        Map<String, Object> keys = generateKeys();

        RSAPublicKey pubKey = RSAUtils.getpublicKey(keys);
        RSAPrivateKey priKey = RSAUtils.getPrivateKey(keys);

        String pubk = Base64.encode(pubKey.getEncoded());
        String prik = Base64.encode(priKey.getEncoded());

        System.out.println("The pubKey is ");
        System.out.println(pubk);
        System.out.println("lenth\t" + pubk.length());
        System.out.println("The priKey is ");
        System.out.println(prik);
        System.out.println("lenth\t" + prik.length());


    }


}
