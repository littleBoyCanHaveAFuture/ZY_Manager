package com.zyh5games.sdk.channel.tianyu;

/**
 * {"commonKey":"JLZGX9SDKJ7DJW7WF","channelGameId":"DD58B347B88A2C737"}
 * 1.lkey 登陆密匙(登陆接口使用)
 * 2.pkey 充值密匙(兑换游戏币接口使用)
 *
 * @author song minghua
 * @date 2020/07/20
 */
public class TianYuConfig {
    /**
     * 渠道 渠道游戏id
     */
    public static final String GAME_ID = "channelGameId";
    /**
     * 渠道秘钥键值
     */
    public static final String COMMON_KEY = "commonKey";
    public static final String Login_URL = "http://release.tianyuyou.cn/api/notice/gamecp/checkusertoken.php";
}
