package com.zyh5games.sdk.channel.game1758;

/**
 * @author song minghua
 * @date 2020/5/21
 */
public class Game1758Config {
    /**
     * 登陆校验
     */
    public static final String LOGIN_URL = "http://api.1758.com/auth/v4.1/verifyUser.json";

    /**
     * 下单
     */
    public static final String PAY_URL = "http://api.1758.com/pay/v5/unifiedorder.json";
    /**
     * 渠道登录秘钥键值
     */
    public static final String GAME_SECRET = "GameSecret";
    /**
     * 渠道的支付秘钥键值
     */
    public static final String GAME_KEY = "GameKey";
}
