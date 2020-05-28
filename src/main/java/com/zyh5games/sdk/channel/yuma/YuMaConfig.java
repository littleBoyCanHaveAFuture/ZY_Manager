package com.zyh5games.sdk.channel.yuma;

/**
 * 鱼马接的quick
 *
 * @author song minghua
 * @date 2020/5/21
 */
public class YuMaConfig {
    /**
     * quick 登陆地址
     */
    public static final String LOGIN_URL = "https://qkh5api.quickapi.net/webGame/loginApi?";
    /**
     * quick 登陆校验地址
     */
    public static final String LOGIN_CHECK_URL = "http://checkuser.sdk.quicksdk.net/v2/checkUserInfo?";
    /**
     * quick 调起支付地址
     */
    public static final String PAY_URL = "https://qkh5api.quickapi.net/webGame/ajaxGetOrderNo?";
    /**
     * 渠道订单校验地址
     */
    public static final String CHECK_ORDER_URL = "";
    /**
     * quick 游戏id键值
     */
    public static final String PRODUCT_CODE = "ProductCode";
    /**
     * quick 游戏秘钥键值
     */
    public static final String PRODUCT_KEY = "ProductKey";
    /**
     * quick 支付回调秘钥键值
     */
    public static final String PAY_KEY = "Callback_Key";
    /**
     * quick MD5秘钥键值
     */
    public static final String MD5_KEY = "Md5_Key";

}
