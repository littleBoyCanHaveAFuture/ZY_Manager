package com.zyh5games.util;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * RSA公钥/私钥/签名工具包
 * </p>
 * <p>
 * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
 * </p>
 * <p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 *
 * @author IceWee
 * @author song minghua
 * @version 1.0
 * <p>
 * ————————————————
 * 版权声明：本文为CSDN博主「yfx000」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/yfx000/article/details/79014920
 * /**
 * @date 2012-4-26
 * @date 2019/12/23
 */
public class RSAUtils {


    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param msg        已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(String msg, String privateKey) throws Exception {
        byte[] data = msg.getBytes();
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param msg       已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(String msg, String publicKey, String sign)
            throws Exception {
        byte[] data = msg.getBytes();
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(sign));
    }

    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedDataStr 已加密数据
     * @param privateKey       私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String encryptedDataStr, String privateKey)
            throws Exception {
        byte[] encryptedData = Base64.getDecoder().decode(encryptedDataStr);
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }

    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedDataStr 已加密数据
     * @param publicKey        公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String encryptedDataStr, String publicKey)
            throws Exception {
        byte[] encryptedData = Base64.getDecoder().decode(encryptedDataStr);
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }

    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param msg       源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String msg, String publicKey)
            throws Exception {
        byte[] data = msg.getBytes();
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();

        String encryptedDataStr = Base64.getEncoder().encodeToString(encryptedData);
        return encryptedDataStr;
    }

    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param msg        源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(String msg, String privateKey)
            throws Exception {
        byte[] data = msg.getBytes();
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();

        String encryptedDataStr = Base64.getEncoder().encodeToString(encryptedData);
        return encryptedDataStr;
    }

    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String Sign(int accountID,
                              String channelOrderID,
                              String productID,
                              String productName,
                              String productDesc,
                              int money,
                              String roleID,
                              String roleName,
                              String roleLevel,
                              String serverID,
                              String serverName,
                              String extension,
                              Integer status,
                              String notifyUrl,
                              String signType,
                              String sign) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        sb.append("accountID=").append(accountID).append("&")
                .append("channelOrderID=").append(channelOrderID == null ? "" : channelOrderID).append("&")
                .append("productID=").append(productID == null ? "" : productID).append("&")
                .append("productName=").append(productName == null ? "" : productName).append("&")
                .append("productDesc=").append(productDesc == null ? "" : productDesc).append("&")
                .append("money=").append(money).append("&")
                .append("roleID=").append(roleID == null ? "" : roleID).append("&")
                .append("roleName=").append(roleName == null ? "" : roleName).append("&")
                .append("roleLevel=").append(roleLevel == null ? "" : roleLevel).append("&")
                .append("serverID=").append(serverID == null ? "" : serverID).append("&")
                .append("serverName=").append(serverName == null ? "" : serverName).append("&")
                .append("extension=").append(extension == null ? "" : extension)
                .append("status=").append(status == null ? "" : status);
        if (!StringUtils.isEmpty(notifyUrl)) {
            sb.append("&notifyUrl=").append(notifyUrl);
        }

        return sb.toString();
    }

    public static boolean isSignOK(String accountID, String channelID, String channelUid, String appID, String channelOrderID,
                                   String productID, String productName, String productDesc, String money,
                                   String roleID, String roleName, String roleLevel,
                                   String serverID, String serverName,
                                   String realMoney, String completeTime, String sdkOrderTime,
                                   String status, String notifyUrl,
                                   String signType,
                                   String sign) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        sb.append("accountID=").append(accountID).append("&")
                .append("channelID=").append(channelID).append("&")
                .append("channelUid=").append(channelUid).append("&")

                .append("appID=").append(appID).append("&")
                .append("channelOrderID=").append(channelOrderID == null ? "" : channelOrderID).append("&")

                .append("productID=").append(productID).append("&")
                .append("productName=").append(productName).append("&")
                .append("productDesc=").append(productDesc).append("&")
                .append("money=").append(money).append("&")

                .append("roleID=").append(roleID).append("&")
                .append("roleName=").append(roleName == null ? "" : roleName).append("&")
                .append("roleLevel=").append(roleLevel == null ? "" : roleLevel).append("&")

                .append("serverID=").append(serverID).append("&")
                .append("serverName=").append(serverName == null ? "" : serverName).append("&")

                .append("realMoney=").append(realMoney == null ? "" : realMoney).append("&")
                .append("completeTime=").append(completeTime).append("&")
                .append("sdkOrderTime=").append(sdkOrderTime == null ? "" : sdkOrderTime).append("&")

                .append("status=").append(status)
                .append("&notifyUrl=").append(notifyUrl);

        String encoded = URLEncoder.encode(sb.toString(), "UTF-8");

        String newSign = EncryptUtils.md5(encoded).toLowerCase();

        System.out.println("Md5 sign recv  \n:" + sign);
        System.out.println("Md5 sign server\n:" + newSign);

        return newSign.equals(sign);
    }

    public static void main(String[] args) throws Exception {
        String publicKey = "";
        String privateKey = "";
        //获得公钥和私钥
        try {
            Map<String, Object> keyMap = RSAUtils.genKeyPair();
            publicKey = RSAUtils.getPublicKey(keyMap);
            privateKey = RSAUtils.getPrivateKey(keyMap);
            System.err.println("公钥: \n" + publicKey);
            System.err.println("私钥： \n" + privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String source = RSAUtils.Sign(100000,
                "100100",
                "100",
                "大还丹",
                "回复满血",
                600,
                "100",
                "roleName",
                "666", "1", "龙飞凤舞", "qqq",
                1, "", "rsa", "");

        System.out.println("sign " + source);
        System.out.println("==========================================1");
        System.err.println("公钥加密——私钥解密");
        System.out.println("\r加密前文字：\r\n" + source);


        String encodedData = RSAUtils.encryptByPublicKey(source, publicKey);


        System.out.println("加密后文字：\r\n" + encodedData);

        String decodedData = RSAUtils.decryptByPrivateKey(encodedData, privateKey);

        System.out.println("解密后文字: \r\n" + decodedData);

    }
}
