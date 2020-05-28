package com.zyh5games.sdk.channel.ziwan;

/**
 * @author song minghua
 * @date 2020/5/21
 */
public class ZiWanConfig {
    /**
     * 登陆校验地址
     */
    public static final String LOGIN_URL = "https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=get_userinfo";
    /**
     * 渠道支付校验地址
     */
    public static final String PAY_URL = "https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=pay_info";
    /**
     * 渠道订单校验地址
     */
    public static final String CHECK_ORDER_URL = "https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=check_order";
    /**
     * 渠道秘钥键值
     */
    public static final String KEY = "appsecret";
    /**
     * 渠道的渠道id键值
     */
    public static final String CHANNEL_ID = "channel_id";
}
